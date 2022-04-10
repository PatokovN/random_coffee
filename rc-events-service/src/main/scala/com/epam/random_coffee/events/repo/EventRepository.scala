package com.epam.random_coffee.events.repo

import com.epam.random_coffee.events.model.Event
import scala.concurrent.Future

trait EventRepository {
  def save (event: Event): Future[Unit]

  def find (event: Event): Future[Option[Event]]

  def refresh (oldEvent: Event, newEvent: Event): Future[Unit]

  def delete (event: Event): Future[Unit]
}
