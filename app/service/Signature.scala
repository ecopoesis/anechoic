package service

import play.api.Play
import play.api.Play.current
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Mac

object Signature {

  def sign(strings: String*): String = {
    internalSign(strings)
  }

  private def internalSign(strings: Seq[String]): String = {
    // build the cleartext
    var clear = ""
    for(s <- strings) {
      clear = clear + s
    }

    // get a hmac_sha1 key
    val keyBytes = Play.configuration.getString("application.secret").get.getBytes
    val signingKey = new SecretKeySpec(keyBytes, "HmacSHA1")

    // get a hmac_sha1 mac instance
    val mac = Mac.getInstance("HmacSHA1")
    mac.init(signingKey)

    // compute the hmac
    val raw = mac.doFinal(clear.getBytes)
    new sun.misc.BASE64Encoder().encode(raw)
  }

  def check(hash: String, strings: String*): Boolean = {
    hash == internalSign(strings)
  }
}
