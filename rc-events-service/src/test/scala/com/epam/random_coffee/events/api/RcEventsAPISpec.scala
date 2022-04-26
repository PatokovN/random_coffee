package com.epam.random_coffee.events.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.epam.random_coffee.events.api.codecs.EventCodecs._
import com.epam.random_coffee.events.api.request.{ CreateEventRequest, UpdateEventRequest }
import com.epam.random_coffee.events.api.response.EventResponse
import com.epam.random_coffee.events.model.Event
import com.epam.random_coffee.events.services.EventService
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.Future

class RcEventsAPISpec extends AnyWordSpec with MockFactory with OneInstancePerTest with ScalatestRouteTest {

  private val eventService = mock[EventService]
  private val eventAPI = new RcEventsAPI(eventService)
  private val routes = Route.seal(eventAPI.routes)

  private val event = Event(1, "create_event")

  private val createEventRequest = CreateEventRequest("created_event")

  private val updateEventRequest = UpdateEventRequest("updated_event")

  private val eventResponse = EventResponse(1, "created_event")

  private val eventResponseUpdated = EventResponse(1, "updated_event")

  "RcEventsAPI" should {
    "return a newly created event" when {
      "user create event" in {
        (eventService.create _).expects(createEventRequest).returns(Future(eventResponse))

        Post("/v1/events", createEventRequest) ~> routes ~> check {
          assert(status == StatusCodes.OK)
        }
      }
    }

    "find existed event" when {
      "user search event" in {
        (eventService.find _).expects(event.id).returns(Future.successful(Option(event)))

        Get("/v1/events/1") ~> routes ~> check {
          assert(status == StatusCodes.OK)
        }
      }
    }

    "delete existed event" when {
      "user delete event" in {
        (eventService.delete _).expects(event.id).returns(Future.unit)

        Delete("/v1/events/1") ~> routes ~> check {
          assert(status == StatusCodes.OK)
        }
      }
    }

    "update existed event" when {
      "user update event" in {
        (eventService.updateEvent _)
          .expects(event.id, updateEventRequest)
          .returns(Future.successful(eventResponseUpdated))

        Put("/v1/events/1", updateEventRequest) ~> routes ~> check {
          assert(status == StatusCodes.OK)
        }
      }
    }

    "fail with 500" when {
      "user's attempt to create event which is already exist" in {
        (eventService.create _).expects(*).returns(Future.failed(new RuntimeException))

        Post("/v1/events", createEventRequest) ~> routes ~> check {
          assert(status == StatusCodes.InternalServerError)
        }
      }

      "user's attempt to delete a non-existent event" in {
        (eventService.delete _).expects(*).returns(Future.failed(new RuntimeException))

        Delete("/v1/events/1") ~> routes ~> check {
          assert(status == StatusCodes.InternalServerError)
        }
      }

      "user's attempt to update a non-existent event" in {
        (eventService.updateEvent _).expects(*, *).returns(Future.failed(new RuntimeException))

        Put("/v1/events/1", updateEventRequest) ~> routes ~> check {
          assert(status == StatusCodes.InternalServerError)
        }
      }
    }
  }
}
