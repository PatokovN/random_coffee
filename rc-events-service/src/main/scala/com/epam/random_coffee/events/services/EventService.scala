package com.epam.random_coffee.events.services

import com.epam.random_coffee.events.model.Event

import scala.concurrent.Future

trait EventService {

  def create(eventName: String): Future[Event]

  def find(id: String): Future[Option[Event]]

  def update(id: String, newEventName: String): Future[Event]

  def delete(id: String): Future[Unit]
}
