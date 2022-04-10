package com.epam.random_coffee.events.api.codecs

import com.epam.random_coffee.events.api.request.{CreateEventRequest, DeleteEventRequest, FindEventRequest, UpdateEventRequest}
import com.epam.random_coffee.events.api.response.{CreateEventResponse, DeleteEventResponse, FindEventResponse, UpdateEventResponse}
import com.epam.random_coffee.events.json.ModelCodecs
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

trait EventCodecs extends ModelCodecs with FailFastCirceSupport {

  implicit lazy val createEventRequestCodec: Codec[CreateEventRequest] = deriveCodec
  implicit lazy val deleteEventRequestCodec: Codec[DeleteEventRequest] = deriveCodec
  implicit lazy val findEventRequestCodec: Codec[FindEventRequest] = deriveCodec
  implicit lazy val updateEventRequestCodec: Codec[UpdateEventRequest] = deriveCodec

  implicit lazy val createEventResponseCodec: Codec[CreateEventResponse] = deriveCodec
  implicit lazy val deleteEventResponseCodec: Codec[DeleteEventResponse] = deriveCodec
  implicit lazy val findEventResponseCodec: Codec[FindEventResponse] = deriveCodec
  implicit lazy val updateEventResponseCodec: Codec[UpdateEventResponse] = deriveCodec

}

object EventCodecs extends EventCodecs
