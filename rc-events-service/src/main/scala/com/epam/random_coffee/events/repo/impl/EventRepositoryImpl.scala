package com.epam.random_coffee.events.repo.impl

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import com.epam.random_coffee.events.api.request.UpdateEventRequest
import com.epam.random_coffee.events.api.response.EventResponse
import com.epam.random_coffee.events.model.Event
import com.epam.random_coffee.events.repo.EventRepository
import doobie.Transactor
import doobie.implicits._

import scala.concurrent.Future

class EventRepositoryImpl(transactor: Transactor[IO])(implicit runtime: IORuntime) extends EventRepository {

  override def save(eventName: String): Future[Unit] = unsafeRun {
    sql"""insert into "event" (name) values ($eventName)""".update.run.map(_ => ())
  }

  override def findByName(eventName: String): Future[Option[EventResponse]] = unsafeRun {
    sql"""select id, name from "event" where name = $eventName""".query[EventResponse].option
  }

  override def findById(id: Int): Future[Option[Event]] = unsafeRun {
    sql"""select id, name from "event" where id = $id""".query[Event].option
  }

  override def refresh(id: Int, newEventName: UpdateEventRequest): Future[Unit] = unsafeRun {
    sql"""update "event" set name = ${newEventName.eventName} where id = $id""".update.run.map(_ => ())
  }

  override def delete(id: Int): Future[Unit] = unsafeRun {
    sql"""delete from "event" where id = $id""".update.run.map(_ => ())
  }

  private def unsafeRun[T](query: doobie.ConnectionIO[T]): Future[T] =
    query.transact(transactor).unsafeToFuture()

}
