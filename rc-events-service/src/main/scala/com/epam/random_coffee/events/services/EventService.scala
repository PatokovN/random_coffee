package com.epam.random_coffee.events.services

import com.epam.random_coffee.events.model.Event
import scala.concurrent.Future

trait EventService {

  def create (event: Event): Future[Event]

  def find (event: Event): Future[Option[Event]]

  def updateEvent (oldEventName: Event, newEventName: Event): Future[Event]

  def remove (event: Event): Future[Event]
}
