package com.epam.random_coffee.events.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.epam.random_coffee.events.api.codecs.EventCodecs
import com.epam.random_coffee.events.api.request.{CreateEventRequest, DeleteEventRequest, FindEventRequest, UpdateEventRequest}
import com.epam.random_coffee.events.api.response.{CreateEventResponse, DeleteEventResponse, FindEventResponse, UpdateEventResponse}
import com.epam.random_coffee.events.services.EventService

class RcEventsAPI(eventService: EventService) extends EventCodecs {

  def routes: Route = pathPrefix("events" / "v1")(create ~ delete ~ find ~ update)

  private lazy val create: Route =
    (path("create") & post & entity(as[CreateEventRequest])) { request =>
      onSuccess(eventService.create(request.event)){status =>
        complete(CreateEventResponse(status))
      }
    }

  private lazy val delete: Route =
    (path("delete") & post & entity(as[DeleteEventRequest])) { request =>
      onSuccess(eventService.remove(request.event)) { status =>
        complete(DeleteEventResponse(status, "deleted"))
      }
    }

  private lazy val find: Route =
    (path("find") & post & entity(as[FindEventRequest])) { request =>
      onSuccess(eventService.find(request.event)) {status =>
        complete(FindEventResponse(if (status.isDefined)"found " else "Not found",status))
      }
    }

  private lazy val update: Route =
    (path("update") & post & entity(as[UpdateEventRequest])) {request =>
      onSuccess(eventService.updateEvent(request.oldEvent,request.newEvent)) {status =>
        complete(UpdateEventResponse(request.oldEvent, "replaced by",status))
      }
    }

}
