package com.epam.random_coffee.events.services

import com.epam.random_coffee.events.api.request.{ CreateEventRequest, UpdateEventRequest }
import com.epam.random_coffee.events.api.response.EventResponse
import com.epam.random_coffee.events.model.Event

import scala.concurrent.Future

trait EventService {

  def create(event: CreateEventRequest): Future[EventResponse]

  def find(id: Int): Future[Option[Event]]

  def updateEvent(id: Int, event: UpdateEventRequest): Future[EventResponse]

  def delete(id: Int): Future[Unit]
}
