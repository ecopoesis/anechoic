package helpers

import org.specs2.mutable.Specification
import play.api.test.Helpers._
import play.api.test.FakeApplication

class CryptoSpec extends Specification {
  "Crypto" should {
    "generate identical keys for the same password" in test {
      val s1 = new String(Crypto.generateSecret("foobar").getEncoded)
      val s2 = new String(Crypto.generateSecret("foobar").getEncoded)

      s1 must_== s2
    }

    "encrypt and decrypt" in test {
      val cleartext = "Hello World"
      val password = "password"
      val (ciphertext, iv) = Crypto.aesEncrypt(cleartext, password)
      cleartext must_!= ciphertext

      Crypto.aesDecrypt(ciphertext, iv, password) must_== cleartext
     }
  }

  def test[T](code: =>T) =
    running(FakeApplication())(code)
}