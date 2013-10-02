package controllers

import securesocial.controllers.TemplatesPlugin
import play.api.mvc.{RequestHeader, Request}
import play.api.templates.{Html, Txt}
import play.api.{Logger, Plugin, Application}
import securesocial.core.{Identity, SecuredRequest, SocialUser}
import play.api.data.Form
import securesocial.controllers.Registration.RegistrationInfo
import securesocial.controllers.PasswordChange.ChangeInfo

class Authorization(application: play.Application) extends TemplatesPlugin {

  /**
   * Returns the html for the login page
   */
  override def getLoginPage[A](implicit request: Request[A], form: Form[(String, String)],  msg: Option[String] = None): Html = {
    views.html.authorization.login(form, msg)
  }

  /**
   * NOT USED - Returns the html for the signup page
   */
  override def getSignUpPage[A](implicit request: Request[A], form: Form[RegistrationInfo], token: String): Html = {
    views.html.authorization.registration.unused.signUp(form, token)
  }

  /**
   * NOT USED - Returns the html for the start signup page
   */
  override def getStartSignUpPage[A](implicit request: Request[A], form: Form[String]): Html = {
    views.html.authorization.registration.unused.startSignUp(form)
  }

  /**
   * Returns the html for the reset password page
   */
  override def getStartResetPasswordPage[A](implicit request: Request[A], form: Form[String]): Html = {
    views.html.authorization.registration.startResetPassword(form)
  }

  /**
   * Returns the html for the start reset page
   */
  def getResetPasswordPage[A](implicit request: Request[A], form: Form[(String, String)], token: String): Html = {
    views.html.authorization.registration.resetPasswordPage(form, token)
  }

  /**
   * Returns the html for the change password page
   */
  def getPasswordChangePage[A](implicit request: SecuredRequest[A], form: Form[ChangeInfo]): Html = {
    views.html.authorization.passwordChange(form)
  }

  /**
   * Returns the email sent when a user starts the sign up process
   */
  def getSignUpEmail(token: String)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
    (None, Option(views.html.authorization.mails.signUpEmail(token)))
  }

  /**
   * Returns the email sent when the user is already registered
   */
  def getAlreadyRegisteredEmail(user: Identity)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
    (None, Option(views.html.authorization.mails.alreadyRegisteredEmail(user)))
  }

  /**
   * Returns the welcome email sent when the user finished the sign up process
   */
  def getWelcomeEmail(user: Identity)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
    (None, Option(views.html.authorization.mails.welcomeEmail(user)))
  }

  /**
   * Returns the email sent when a user tries to reset the password but there is no account for
   * that email address in the system
   */
  def getUnknownEmailNotice()(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
    (None, Option(views.html.authorization.mails.unknownEmailNotice(request)))
  }

  /**
   * Returns the email sent to the user to reset the password
   */
  def getSendPasswordResetEmail(user: Identity, token: String)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
    (None, Option(views.html.authorization.mails.passwordResetEmail(user, token)))
  }

  /**
   * Returns the email sent as a confirmation of a password change
   */
  def getPasswordChangedNoticeEmail(user: Identity)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
    (None, Option(views.html.authorization.mails.passwordChangedNotice(user)))
  }

  def getNotAuthorizedPage[A](implicit request: Request[A]): Html = {
    views.html.authorization.notAuthorized(request)
  }
}