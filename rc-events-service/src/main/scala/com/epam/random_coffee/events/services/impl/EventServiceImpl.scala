package com.epam.random_coffee.events.services.impl

import com.epam.random_coffee.events.model.Event
import com.epam.random_coffee.events.repo.EventRepository
import com.epam.random_coffee.events.services.EventService

import scala.concurrent.{ExecutionContext, Future}

class EventServiceImpl(repo: EventRepository)(implicit ec: ExecutionContext) extends EventService{

  override def create (event: Event): Future[Event] =
    for {
      existingEvent <- repo.find(event)
      _ <- existingEvent.fold(Future.unit)(_ => duplicateEventErr(event))
      _ <- save(event)
    } yield event

  private def save(event: Event): Future[Unit] = repo.save(event)

  override def find(event: Event): Future[Option[Event]] = repo.find(event)

  override def remove(event: Event): Future[Event] =
    for {
      existingEvent <- repo.find(event)
      _ <- existingEvent.fold(absentEventErr(event))(_ => Future.unit)
      _ <- delete(event)
    } yield event

  private def delete(event: Event): Future[Unit] = repo.delete(event)

  override def updateEvent(oldEventName: Event, newEventName: Event): Future[Event] =
    for {
      existingEvent <- repo.find(oldEventName)
      _ <- existingEvent.fold(absentEventErr(oldEventName))(_ => Future.unit)
      _ <- refresh(oldEventName, newEventName)
    } yield newEventName

   def refresh(oldEventName: Event, newEventName: Event): Future[Unit] = {

    repo.refresh(oldEventName,newEventName)
  }

  private def duplicateEventErr(event: Event): Future[Unit] =
  Future.failed(new IllegalArgumentException(s"Event ${event.eventName} already exists"))

  private def absentEventErr(event: Event): Future[Unit] =
  Future.failed(new IllegalArgumentException(s"Event ${event.eventName} doesn't exist"))

}
