package helpers

import org.specs2.mutable.Specification

class FormattingSpec extends Specification {
  "Formatting" should {
    "urlify 'Stupid Story  123!'" in {
      Formatting.urlify("Stupid Story  123!") must_== "Stupid_Story_123"
    }
  }
}