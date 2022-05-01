package com.epam.random_coffee.events.services.impl

import com.epam.random_coffee.events.model.EventId
import doobie.Meta

trait Doobie_support {

  implicit lazy val eventIdMeta: Meta[EventId] = Meta.StringMeta.imap(EventId)(_.value)

}
