package com.epam.random_coffee.events.repo.impl

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import com.epam.random_coffee.events.model.Event
import com.epam.random_coffee.events.repo.EventRepository
import doobie.Transactor
import doobie.implicits._

import scala.concurrent.Future


class EventRepositoryImpl (transactor: Transactor[IO])(implicit runtime: IORuntime)
  extends EventRepository
  with DoobieSupport {

  override def save(event: Event): Future[Unit] = unsafeRun{
    sql"""insert into "event" (name) values (${event.eventName})""".update.run
      .map(_ => ())
  }

  override def find(event: Event): Future[Option[Event]] = unsafeRun{
    sql"""select name from "event" where name = ${event.eventName}""".query[Event].option
  }

  override def refresh(oldEvent: Event, newEventName: Event): Future[Unit] = unsafeRun {
    sql"""update "event" set name = ${newEventName.eventName} where name = ${oldEvent.eventName}"""
      .update.run.map(_ => ())
  }

  override def delete(event: Event): Future[Unit] = unsafeRun {
    sql"""delete from "event" where name = ${event.eventName}""".update.run
      .map(_ => ())
  }

  private def unsafeRun[T](query: doobie.ConnectionIO[T]): Future[T] =
    query.transact(transactor).unsafeToFuture()
}
