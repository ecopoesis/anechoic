package service.auth

import securesocial.core.IdGenerator
import play.api.Application
import java.security.SecureRandom
import play.api.libs.Codecs

class MemcachedIdGenerator(app: Application) extends IdGenerator(app) {

  val random = new SecureRandom()
  val memcachedNsLen = app.configuration.getString("memcached.namespace").map(_.length).getOrElse(0)
  val idSizeInBytes = 125 - (memcachedNsLen / 2) - (memcachedNsLen % 2)

  /**
   * Generates a new id using SecureRandom
   *
   * @return the generated id
   */
  def generate: String = {
    val randomValue = new Array[Byte](idSizeInBytes)
    random.nextBytes(randomValue)
    Codecs.toHexString(randomValue)
  }
}