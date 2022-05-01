package com.epam.random_coffee.events.repo

import com.epam.random_coffee.events.model.{ Event, EventId }

import scala.concurrent.Future

trait EventRepository {
  def save(event: Event): Future[Unit]

  def find(id: EventId): Future[Option[Event]]

  def update(id: EventId, newEventName: String): Future[Unit]

  def delete(id: EventId): Future[Unit]
}
