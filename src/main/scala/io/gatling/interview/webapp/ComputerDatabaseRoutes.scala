package io.gatling.interview.webapp

import cats.effect._
import cats.implicits._

import io.gatling.interview.api._
import io.gatling.interview.api
import io.gatling.interview.repository.ComputerRepository
import io.gatling.interview.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object ComputerDatabaseRoutes {
  private val localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  private def localDateToString(value: LocalDate): String = localDateFormatter.format(value)
  private def parseDateEither(date: String): Either[String, LocalDate] =
    try {
      Right(LocalDate.parse(date, localDateFormatter))
    } catch {
      case _: DateTimeParseException => Left(s"Invalid date format: $date")
    }

  private def toApiComputer(value: model.Computer): api.Computer = api.Computer(
    id = value.id,
    name = value.name,
    introduced = value.introduced.map(localDateToString),
    discontinued = value.discontinued.map(localDateToString)
  )

  private def fromApiComputer(input: CreateComputerPayload): Either[String, model.Computer] = {
    val introducedDate = input.introduced.traverse(parseDateEither)
    val discontinuedDate = input.discontinued.traverse(parseDateEither)

    (introducedDate, discontinuedDate).mapN { (introduced, discontinued) =>
      model.Computer(
        id = 0,
        name = input.name,
        introduced = introduced,
        discontinued = discontinued
      )
    }
  }
}

class ComputerDatabaseRoutes(repository: ComputerRepository[IO])
    extends ComputerDatabaseEndpoints[IO] {
  import ComputerDatabaseRoutes._

  override def listComputers(): IO[ComputersOutput] = for {
    result <- repository.fetchAll()
  } yield ComputersOutput(result.map(toApiComputer))

  override def getComputer(id: Long): IO[Computer] = {
    repository.fetch(id).map(toApiComputer)
  }

  override def createComputer(payload: CreateComputerPayload): IO[Computer] = {
    fromApiComputer(payload) match {
      case Right(newComputer) =>
        for {
          insertedComputer <- repository.insert(newComputer)
        } yield toApiComputer(insertedComputer)
      case Left(errorMessage) =>
        IO.raiseError(new IllegalArgumentException(errorMessage))
    }
  }
}
