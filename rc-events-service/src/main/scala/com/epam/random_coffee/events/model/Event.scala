package com.epam.random_coffee.events.model

final case class Event(id: EventId, eventName: String)

final case class EventId(value: String) extends AnyVal
