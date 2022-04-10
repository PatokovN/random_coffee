package com.epam.random_coffee.events.json

import com.epam.random_coffee.events.model.Event
import io.circe.{Codec, Decoder, Encoder}

trait ModelCodecs {

  implicit lazy val eventCodec: Codec[Event] =
    Codec.from(Decoder.decodeString.map(Event), Encoder.encodeString.contramap(_.eventName))

}

object ModelCodecs extends ModelCodecs

