package com.epam.random_coffee.events.repo.impl
import com.epam.random_coffee.events.model.Event
import doobie._

trait DoobieSupport {

  implicit lazy val eventNameMeta: Meta[Event] = Meta.StringMeta.imap(Event)(_.eventName)
}
