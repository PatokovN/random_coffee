package com.epam.random_coffee.events.api.response

import com.epam.random_coffee.events.model.Event

case class UpdateEventResponse(oldEvent: Event, status: String, newEvent: Event)
