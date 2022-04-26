package com.epam.random_coffee.events.services.impl

import com.epam.random_coffee.events.api.request.{ CreateEventRequest, UpdateEventRequest }
import com.epam.random_coffee.events.api.response.EventResponse
import com.epam.random_coffee.events.model.Event
import com.epam.random_coffee.events.repo.EventRepository
import com.epam.random_coffee.events.services.EventService

import scala.concurrent.{ ExecutionContext, Future }

class EventServiceImpl(repo: EventRepository)(implicit ec: ExecutionContext) extends EventService {

  override def create(request: CreateEventRequest): Future[EventResponse] =
    for {
      existingEvent <- repo.findByName(request.eventName)
      _ <- existingEvent.fold(Future.unit)(_ => duplicateEventErr(request.eventName))
      _ <- repo.save(request.eventName)
      responseEvent <- repo.findByName(request.eventName)
      response = responseEvent.get
    } yield response

  override def find(id: Int): Future[Option[Event]] =
    for {
      notVerifiedEvent <- repo.findById(id)
      _ <- notVerifiedEvent.fold(absentEventErr(id))(_ => Future.unit)
      verifiedEvent = notVerifiedEvent
    } yield verifiedEvent

  override def delete(id: Int): Future[Unit] =
    for {
      existingEvent <- repo.findById(id)
      nothing <- existingEvent.fold(absentEventErr(id))(_ => Future.unit)
      _ <- repo.delete(id)
    } yield nothing

  override def updateEvent(id: Int, newEventName: UpdateEventRequest): Future[EventResponse] =
    for {
      existingEvent <- repo.findById(id)
      _ <- existingEvent.fold(absentEventErr(id))(_ => Future.unit)
      _ <- repo.refresh(id, newEventName)
      response = EventResponse(id, newEventName.eventName)
    } yield response

  private def duplicateEventErr(event: String): Future[Unit] =
    Future.failed(new IllegalArgumentException(s"Event $event already exists"))

  private def absentEventErr(id: Int): Future[Unit] =
    Future.failed(new IllegalArgumentException(s"Event with id № $id doesn't exist"))

}
