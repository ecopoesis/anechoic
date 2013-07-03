package dao

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._

class StoriesDaoSpec extends Specification {

   // a step to execute before the specification must be declared first
  step {

  }

  "StoriesDao" should {
      "have 0 stories" in testDb {
           StoriesDao.get(0, 0) must have size 0
      }

      "start with 'Hello'" in {
        "Hello world" must startWith("Hello")
      }
      /**
       * a failing example will stop right away, without having to "chain" expectations
       */
      "with 'world'" in {
        // Expectations are throwing exception by default so uncommenting this line will
        // stop the execution right away with a Failure
        // "Hello world" must startWith("Hi")
  
        "Hello world" must endWith("world")
      }
    }

  object context extends org.specs2.mutable.Before {
    def before = { "foo".pp }
  }

  def testDb[T](code: =>T) =
    running(FakeApplication(additionalConfiguration = Map(
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url"    -> "jdbc:h2:mem:anechoic;MODE=MySQL"
    )))(code)
}