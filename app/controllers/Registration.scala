package controllers

import _root_.java.util.UUID
import play.api.mvc.{Result, Action, Controller}
import play.api.data._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.{Play, Logger}
import securesocial.core.providers.UsernamePasswordProvider
import securesocial.core._
import com.typesafe.plugin._
import Play.current
import securesocial.core.providers.utils._
import org.joda.time.DateTime
import play.api.i18n.Messages
import securesocial.core.providers.Token
import scala.Some
import securesocial.core.IdentityId
import securesocial.controllers.{ProviderController, TemplatesPlugin}
//import play.data.validation.Constraints.EmailValidator
import scala.language.reflectiveCalls

/**
 * this class is a giant hack so we can get single step signup to work
 * in the future, it SecureSocial should be rebuilt to handle this natively
 */
object Registration extends Controller {

  private def stringConfig(key: String, default: => String) = {
    Play.current.configuration.getString(key).getOrElse(default)
  }
  case class RegInfo(userName: String, firstName: String, lastName: String, password: String, email: String)

  val form = Form[RegInfo](
    mapping(
      "userName" -> nonEmptyText.verifying(Messages("securesocial.signup.userNameAlreadyTaken"), userName => {
        UserService.find(IdentityId(userName,UsernamePasswordProvider.UsernamePassword)).isEmpty
      }),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      ("password" ->
        tuple(
          "password1" -> nonEmptyText.verifying( use[PasswordValidator].errorMessage,
            p => use[PasswordValidator].isValid(p)
          ),
          "password2" -> nonEmptyText
        ).verifying(Messages("securesocial.signup.passwordsDoNotMatch"), passwords => passwords._1 == passwords._2)
        ),
      "email" -> email.verifying(nonEmpty)
    )
      // binding
      ((userName, firstName, lastName, password, email) => RegInfo(userName, firstName, lastName, password._1, email))
      // unbinding
      (info => Some(info.userName, info.firstName, info.lastName, ("", ""), info.email))
  )

  def signup = Action { implicit request =>
    Ok(views.html.authorization.registration.singleSignUp(form))
  }

  def handleSignup = Action { implicit request =>
    form.bindFromRequest.fold (
      errors => {
        BadRequest(views.html.authorization.registration.singleSignUp(errors))
      },
      info => {
        // check if there is already an account for this email address
        UserService.findByEmailAndProvider(info.email, UsernamePasswordProvider.UsernamePassword) match {
          case Some(user) => {
            Redirect("/signup").flashing("error" -> Messages("registration.duplicate.email"))
          }
          case _ => {
            val identityId = IdentityId(info.userName, UsernamePasswordProvider.UsernamePassword)
            val user = SocialUser(
              identityId,
              info.firstName,
              info.lastName,
              "%s %s".format(info.firstName, info.lastName),
              Some(info.email),
              GravatarHelper.avatarFor(info.email),
              AuthenticationMethod.UserPassword,
              passwordInfo = Some(Registry.hashers.currentHasher.hash(info.password))
            )
            val saved = UserService.save(user)
            if (UsernamePasswordProvider.sendWelcomeEmail) {
              Mailer.sendWelcomeEmail(saved)
            }
            val eventSession = Events.fire(new SignUpEvent(user)).getOrElse(session)
            ProviderController.completeAuthentication(user, eventSession).flashing("success" -> Messages("securesocial.signup.signUpDone"))
          }
        }
      }
    )
  }
}
