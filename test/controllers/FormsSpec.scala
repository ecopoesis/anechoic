package controllers

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import helpers.Validation

class FormsSpec extends Specification {

  "Validation" should {
    "validate 'http://www.anechoicnews.com'" in {
      Validation.url("http://www.anechoicnews.com") must_== true
    }

    "validate 'https://www.anechoicnews.com'" in {
      Validation.url("https://www.anechoicnews.com") must_== true
    }

    "not validate 'www.anechoicnews.com'" in {
      Validation.url("www.anechoicnews.com") must_== false
    }
  }
}
