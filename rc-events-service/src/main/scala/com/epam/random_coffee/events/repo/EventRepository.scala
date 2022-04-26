package com.epam.random_coffee.events.repo

import com.epam.random_coffee.events.api.request.UpdateEventRequest
import com.epam.random_coffee.events.api.response.EventResponse
import com.epam.random_coffee.events.model.Event

import scala.concurrent.Future

trait EventRepository {
  def save(eventName: String): Future[Unit]

  def findByName(eventName: String): Future[Option[EventResponse]]

  def findById(id: Int): Future[Option[Event]]

  def refresh(id: Int, newEventName: UpdateEventRequest): Future[Unit]

  def delete(id: Int): Future[Unit]
}
