package helpers

object Conversion {

  def hexStringToByteArray(s: String): Array[Byte] = {
    val len = s.length
    val data = new Array[Byte](len / 2)
    for (i <- 0 until len if i % 2 == 0) {
      data(i / 2) = ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16)).toByte;
    }
    return data;
  }

}
