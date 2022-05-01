package com.epam.random_coffee.events.api.response

import com.epam.random_coffee.events.model.{ Event, EventId }

case class EventResponse(id: EventId, eventName: String)

object EventResponse {
  def fromEvent(event: Event): EventResponse = EventResponse(event.id, event.eventName)
}
