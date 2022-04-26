package com.epam.random_coffee.events.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.epam.random_coffee.events.api.codecs.EventCodecs
import com.epam.random_coffee.events.api.request.{ CreateEventRequest, UpdateEventRequest }
import com.epam.random_coffee.events.services.EventService

class RcEventsAPI(eventService: EventService) extends EventCodecs {

  def routes: Route = pathPrefix("v1" / "events")(createEvent ~ deleteEvent ~ getEvent ~ updateEvent)

  private lazy val createEvent: Route =
    (post & entity(as[CreateEventRequest]))(request => complete(eventService.create(request)))

  private lazy val deleteEvent: Route =
    (path(IntNumber) & delete)(id => complete(eventService.delete(id)))

  private lazy val getEvent: Route =
    (path(IntNumber) & get)(id => complete(eventService.find(id)))

  private lazy val updateEvent: Route =
    (path(IntNumber) & put & entity(as[UpdateEventRequest])) { (id, event) =>
      complete(eventService.updateEvent(id, event))
    }

}
