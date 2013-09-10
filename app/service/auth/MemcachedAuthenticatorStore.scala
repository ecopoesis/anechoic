package service.auth

import securesocial.core.{Authenticator, DefaultAuthenticatorStore}
import play.api.Application

class MemcachedAuthenticatorStore(app: Application) extends DefaultAuthenticatorStore(app) {

  override def find(id: String): Either[Error, Option[Authenticator]] =
    if (id.isEmpty)
      Right(None)
    else
      super.find(id)

}