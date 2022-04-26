package com.epam.random_coffee.events.api.codecs

import com.epam.random_coffee.events.api.request.{ CreateEventRequest, UpdateEventRequest }
import com.epam.random_coffee.events.api.response.EventResponse
import com.epam.random_coffee.events.model.Event
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

trait EventCodecs extends FailFastCirceSupport {

  implicit lazy val createEventRequestCodec: Codec[CreateEventRequest] = deriveCodec

  implicit lazy val updateEventRequestCodec: Codec[UpdateEventRequest] = deriveCodec

  implicit lazy val eventResponseCodec: Codec[EventResponse] = deriveCodec

  implicit lazy val eventCodec: Codec[Event] = deriveCodec

}

object EventCodecs extends EventCodecs
