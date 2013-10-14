package helpers

import org.bouncycastle.jce.provider.BouncyCastleProvider
import play.api.Play
import java.io.BufferedInputStream
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import javax.crypto.Cipher
import scala.Predef.String
import org.apache.commons.codec.binary.Base64
import scala.language.postfixOps
import play.api.Play.current

object Crypto {

  val privateKeyPath = "resources/keys/rsa_private.der"

  val key = {
    java.security.Security.addProvider(new BouncyCastleProvider)
    val stream = Play.classloader.getResource(privateKeyPath).openStream
    val bis = new BufferedInputStream(stream)
    val keybytes = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray
    stream.close()
    KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keybytes))
  }

  def rsaDecrypt(s: String): String = {
    val RSADecrypter = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC")
    RSADecrypter.init(Cipher.DECRYPT_MODE, key)
    val bytes = Conversion.hexStringToByteArray(s)
    val clearBytes = RSADecrypter.doFinal(bytes, 0, 256)
    new String(Base64.decodeBase64(clearBytes))
  }

}
