package io.gatling.interview.webapp

import cats.effect._
import io.gatling.interview.api._
import io.gatling.interview.api
import io.gatling.interview.repository.ComputerRepository
import io.gatling.interview.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ComputerDatabaseRoutes {
  private val localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  private def localDateToString(value: LocalDate): String = localDateFormatter.format(value)
  private def stringToLocalDate(value: String): LocalDate =
    LocalDate.parse(value, localDateFormatter)

  private def toApiComputer(value: model.Computer): api.Computer = api.Computer(
    id = value.id,
    name = value.name,
    introduced = value.introduced.map(localDateToString),
    discontinued = value.discontinued.map(localDateToString)
  )

  private def fromApiComputer(payload: api.CreateComputerPayload): model.Computer = model.Computer(
    id = 0,
    name = payload.name,
    introduced = payload.introduced.map(stringToLocalDate),
    discontinued = payload.discontinued.map(stringToLocalDate)
  )
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

  override def createComputer(payload: CreateComputerPayload): IO[Computer] = for {
    newComputer <- repository.insert(fromApiComputer(payload))
  } yield toApiComputer(newComputer)
}
