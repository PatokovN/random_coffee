package com.epam.random_coffee.events.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.epam.random_coffee.events.api.request.{ CreateEventRequest, UpdateEventRequest }
import com.epam.random_coffee.events.model.{ Event, EventId }
import com.epam.random_coffee.events.services.EventService
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest
import org.scalatest.wordspec.AnyWordSpec
import com.epam.random_coffee.events.api.codecs.EventCodecs._

import scala.concurrent.Future

class RcEventsAPISpec extends AnyWordSpec with MockFactory with OneInstancePerTest with ScalatestRouteTest {

  private val eventService = mock[EventService]
  private val eventAPI = new RcEventsAPI(eventService)
  private val routes = Route.seal(eventAPI.routes)
  private val id = EventId("uuid_test")

  private val event = Event(id, "create_event")

  private val updatedEvent = Event(id, "updated_event")

  private val createEventRequest = CreateEventRequest("created_event")

  private val updateEventRequest = UpdateEventRequest("updated_event")

  "RcEventsAPI" should {
    "return a newly created event" when {
      "user create event" in {
        (eventService.create _).expects("created_event").returns(Future.successful(event))

        Post("/events/v1", createEventRequest) ~> routes ~> check {
          assert(status == StatusCodes.OK)
          assert(entityAs[Event] == event)
        }
      }
    }

    "find existed event" when {
      "user search event" in {
        (eventService.find _).expects("uuid_test").returns(Future.successful(Some(event)))

        Get("/events/v1/uuid_test") ~> routes ~> check {
          assert(status == StatusCodes.OK)
          assert(entityAs[Option[Event]].contains(event))
        }
      }
    }

    "delete existed event" when {
      "user delete event" in {
        (eventService.delete _).expects("uuid_test").returns(Future.unit)

        Delete("/events/v1/uuid_test") ~> routes ~> check {
          assert(status == StatusCodes.OK)
        }
      }
    }

    "update existed event" when {
      "user update event" in {
        (eventService.update _)
          .expects("uuid_test", updateEventRequest.eventName)
          .returns(Future.successful(updatedEvent))

        Put("/events/v1/uuid_test", updateEventRequest) ~> routes ~> check {
          assert(status == StatusCodes.OK)
          assert(entityAs[Event] == updatedEvent)
        }
      }
    }

    "fail with 500" when {

      "user's attempt to delete a non-existent event" in {
        (eventService.delete _).expects(*).returns(Future.failed(new RuntimeException))

        Delete("/events/v1/uuid_test") ~> routes ~> check {
          assert(status == StatusCodes.InternalServerError)
        }
      }

      "user's attempt to update a non-existent event" in {
        (eventService.update _).expects(*, *).returns(Future.failed(new RuntimeException))

        Put("/events/v1/uuid_test", updateEventRequest) ~> routes ~> check {
          assert(status == StatusCodes.InternalServerError)
        }
      }
    }
  }
}
