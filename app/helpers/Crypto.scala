package helpers

import org.bouncycastle.jce.provider.BouncyCastleProvider
import play.api.Play
import java.io.BufferedInputStream
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import javax.crypto.{SecretKeyFactory, SecretKey, Cipher}
import org.apache.commons.codec.binary.Base64
import scala.language.postfixOps
import play.api.Play.current
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec, PBEKeySpec}

object Crypto {

  val privateKeyPath = "resources/keys/rsa_private.der"
  val salt = Play.current.configuration.getString("application.secret").get

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

  def generateSecret(password: String): SecretKey = {
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
    val spec = new PBEKeySpec(password.toCharArray, salt.getBytes, 65536, 256)
    new SecretKeySpec(factory.generateSecret(spec).getEncoded, "AES")
  }

  def aesEncrypt(cleartext: String, password: String) = {
    val secret = generateSecret(password)
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, secret)
    val params = cipher.getParameters
    val iv = new String(Base64.encodeBase64(params.getParameterSpec(classOf[IvParameterSpec]).getIV))
    val ciphertext = new String(Base64.encodeBase64(cipher.doFinal(cleartext.getBytes("UTF-8"))))
    (ciphertext, iv)
  }

  def aesDecrypt(ciphertext: String, iv: String, password: String) = {
    try {
      val secret = generateSecret(password)
      val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
      cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(Base64.decodeBase64(iv.getBytes)))
      new String(cipher.doFinal(Base64.decodeBase64(ciphertext.getBytes)), "UTF-8")
    } catch {
      case e: Exception => ""
    }
  }
}
