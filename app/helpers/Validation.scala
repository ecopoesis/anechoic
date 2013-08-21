package helpers

import org.apache.commons.validator.routines.UrlValidator

object Validation {
  def url(url: String): Boolean = {
    val validator = new UrlValidator(Array("http", "https"))
    validator.isValid(url)
  }
}

