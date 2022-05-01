package com.epam.random_coffee.events.services.impl

import com.epam.random_coffee.events.model.{ Event, EventId }
import com.epam.random_coffee.events.repo.EventRepository
import com.epam.random_coffee.events.services.EventService

import java.util.UUID
import scala.concurrent.{ ExecutionContext, Future }

class EventServiceImpl(repo: EventRepository)(implicit ec: ExecutionContext) extends EventService with Doobie_support {

  override def create(eventName: String): Future[Event] = {
    val id = EventId(UUID.randomUUID().toString)
    val event = Event(id, eventName)
    repo.save(event)
    Future(event)
  }
  override def find(id: String): Future[Option[Event]] = repo.find(EventId(id))

  override def delete(id: String): Future[Unit] = {
    val eventId = EventId(id)
    for {
      existingEvent <- repo.find(eventId)
      nothing <- existingEvent.fold(absentEventErr(eventId))(_ => Future.unit)
      _ <- repo.delete(eventId)
    } yield nothing
  }

  override def update(id: String, newEventName: String): Future[Event] = {
    val eventId = EventId(id)
    for {
      existingEvent <- repo.find(eventId)
      _ <- existingEvent.fold(absentEventErr(eventId))(_ => Future.unit)
      _ <- repo.update(eventId, newEventName)
      event = Event(eventId, newEventName)
    } yield event
  }

  private def absentEventErr(id: EventId): Future[Unit] =
    Future.failed(new IllegalArgumentException(s"Event with id ${id.value} doesn't exist"))

}
