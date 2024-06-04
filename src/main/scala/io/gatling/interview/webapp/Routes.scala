package io.gatling.interview.webapp

import cats.effect._
import cats.syntax.all._
import cats.data.Kleisli
import cats.data.OptionT

import hello._
import io.gatling.interview.api._
import io.gatling.interview.repository.ComputerRepository

import org.http4s._
import org.http4s.syntax.all._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Location
import org.http4s.dsl.io._
import org.http4s.server.middleware._

import smithy4s.http4s.SimpleRestJsonBuilder

object Routes {
  private def computerDatabase(repository: ComputerRepository[IO]): Resource[IO, HttpRoutes[IO]] =
    SimpleRestJsonBuilder.routes(new ComputerDatabaseRoutes(repository)).resource

  private val example: Resource[IO, HttpRoutes[IO]] =
    SimpleRestJsonBuilder.routes(HelloWorldRoutes).resource

  private val docs: HttpRoutes[IO] =
    smithy4s.http4s.swagger.docs[IO](ComputerDatabaseEndpoints, HelloWorldEndpoints)

  private val main: HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO] {}
    import dsl._
    HttpRoutes.of { case GET -> Root =>
      IO.pure(
        Response[IO]()
          .withStatus(Found)
          .withHeaders(Location(uri"/docs"))
      )
    }
  }

  private def errorHandler(routes: HttpRoutes[IO]): HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO] {}
    import dsl._
    Kleisli { req =>
      routes(req).handleErrorWith {
        case e: IllegalArgumentException => OptionT.liftF(BadRequest(e.getMessage))
        case e: NoSuchElementException   => OptionT.liftF(NotFound(e.getMessage))
        case e                           => OptionT.liftF(InternalServerError(e.getMessage))
      }
    }
  }

  def all(repository: ComputerRepository[IO]): Resource[IO, HttpRoutes[IO]] = for {
    example <- example
    computerDatabase <- computerDatabase(repository)
  } yield errorHandler(computerDatabase <+> example <+> docs <+> main)
}
