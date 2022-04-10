package com.epam.random_coffee.events.api.response

import com.epam.random_coffee.events.model.Event

case class FindEventResponse(status: String, eventOpt: Option[Event])
