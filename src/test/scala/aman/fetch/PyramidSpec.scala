package aman.fetch

import cats.effect.IO
import cats.implicits._
import org.http4s._
import org.http4s.implicits._
import org.specs2.matcher.MatchResult

class PyramidSpec extends org.specs2.mutable.Specification {

  "Pyramid" >> {
    "return 200 with query param" >> {
      uriReturns200()
    }
    "return 404 without query param" >> {
      uriReturns404()
    }
    "return true for banana" >> {
      uriReturnsTrue()
    }
    "return false for bandana" >> {
      uriReturnsFalse()
    }
  }

  private[this] def retPyramid(maybeWord: Option[String]): Response[IO] = {
    val word = maybeWord.map(w => s"?word=$w").getOrElse("")
    val getWord = Request[IO](Method.GET, Uri.unsafeFromString(s"/pyramid$word"))
    val pyramid = Pyramid.impl[IO]
    FetchRoutes.pyramidRoutes(pyramid).orNotFound(getWord).unsafeRunSync()
  }

  private[this] def uriReturns200(): MatchResult[Status] =
    retPyramid("foo".some).status must beEqualTo(Status.Ok)

  private[this] def uriReturns404(): MatchResult[Status] =
    retPyramid(Option.empty).status must beEqualTo(Status.NotFound)

  private[this] def uriReturnsTrue(): MatchResult[String] =
    retPyramid("banana".some).as[String].unsafeRunSync() must beEqualTo("{\"word\":\"banana\",\"isPyramid\":true}")

  private[this] def uriReturnsFalse(): MatchResult[String] =
    retPyramid("bandana".some).as[String].unsafeRunSync() must beEqualTo("{\"word\":\"bandana\",\"isPyramid\":false}")
}