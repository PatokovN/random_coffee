package com.epam.random_coffee.events.api.request

import com.epam.random_coffee.events.model.Event

case class UpdateEventRequest(oldEvent: Event, newEvent: Event)

