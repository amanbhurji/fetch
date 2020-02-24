package aman.fetch

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.QueryParamDecoderMatcher

object FetchRoutes {

 def pyramidRoutes[F[_]: Sync](P: Pyramid[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "pyramid" :? WordQueryParamMatcher(word) =>
        for {
          result <- P.test(word)
          resp <- Ok(result)
        } yield resp
    }
  }

  object WordQueryParamMatcher extends QueryParamDecoderMatcher[String]("word")
}
