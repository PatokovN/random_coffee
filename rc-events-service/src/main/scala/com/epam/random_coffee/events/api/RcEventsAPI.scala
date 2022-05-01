package com.epam.random_coffee.events.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.epam.random_coffee.events.api.codecs.EventCodecs
import com.epam.random_coffee.events.api.request.{ CreateEventRequest, UpdateEventRequest }
import com.epam.random_coffee.events.api.response.EventResponse
import com.epam.random_coffee.events.services.EventService
import scala.concurrent.ExecutionContext.Implicits.global

class RcEventsAPI(eventService: EventService) extends EventCodecs {

  def routes: Route = pathPrefix("events" / "v1")(createEvent ~ deleteEvent ~ getEvent ~ updateEvent)

  private lazy val createEvent: Route =
    (post & entity(as[CreateEventRequest]))(
      request =>
        complete(
          eventService.create(request.eventName).map(event => EventResponse.fromEvent(event))
        )
    )

  private lazy val deleteEvent: Route =
    (path(Segment) & delete)(id => complete(eventService.delete(id)))

  private lazy val getEvent: Route =
    (path(Segment) & get)(
      id =>
        complete(
          eventService.find(id).map(optEvent => optEvent.map(event => EventResponse.fromEvent(event)))
        )
    )

  private lazy val updateEvent: Route =
    (path(Segment) & put & entity(as[UpdateEventRequest])) { (id, event) =>
      complete(
        eventService.update(id, event.eventName).map(event => EventResponse.fromEvent(event))
      )
    }

}
