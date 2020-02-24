package aman.fetch

import cats.Applicative
import cats.data.NonEmptyMap
import cats.implicits._
import io.circe.generic.semiauto._
import io.circe.Encoder
import org.http4s.EntityEncoder
import org.http4s.circe._

trait Pyramid[F[_]]{
  def test(word: String): F[Pyramid.Result]
}

object Pyramid {
  implicit def apply[F[_]](implicit ev: Pyramid[F]): Pyramid[F] = ev

  final case class Result(word: String, isPyramid: Boolean)
  object Result {
    implicit val resultEncoder: Encoder[Result] = deriveEncoder[Result]

    implicit def resultEntityEncoder[F[_]: Applicative]: EntityEncoder[F, Result] =
      jsonEncoderOf[F, Result]
  }

  def impl[F[_]: Applicative]: Pyramid[F] = new Pyramid[F]{
    def test(word: String): F[Pyramid.Result] = {
      val isPyramid = word
        .toList
        .toNel
        .exists { chars =>
          val charFrequency: NonEmptyMap[Char, Int] = chars
            .groupByNem(identity)
            .map(_.size)
          val frequencies = charFrequency.toNel.map(_._2)
          val sortedFrequencies = frequencies.sorted
          sortedFrequencies.head == 1 && sortedFrequencies.last == sortedFrequencies.size
        }

      Result(word, isPyramid).pure[F]
    }
  }
}
