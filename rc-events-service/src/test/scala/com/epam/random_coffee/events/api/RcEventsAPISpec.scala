package com.epam.random_coffee.events.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.epam.random_coffee.events.api.codecs.EventCodecs._
import com.epam.random_coffee.events.api.request.{CreateEventRequest, DeleteEventRequest, FindEventRequest, UpdateEventRequest}
import com.epam.random_coffee.events.api.response.{CreateEventResponse, DeleteEventResponse, FindEventResponse, UpdateEventResponse}
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

  private val createEventRequest = CreateEventRequest(Event("test_create"))
  private val createEventResponse = CreateEventResponse(Event("test_create"))

  private val findEventRequest = FindEventRequest(Event("test_find"))
  private val findEventResponse = FindEventResponse("found ", Some(Event("test_find")))

  private val deleteEventRequest = DeleteEventRequest(Event("test_delete"))
  private val deleteEventResponse = DeleteEventResponse(Event("test_delete"),"deleted")

  private val updateEventRequest = UpdateEventRequest(Event("Old_Event"), Event("New_Event"))
  private val updateEventResponse = UpdateEventResponse(Event("Old_Event"),"replaced by", Event("New_Event"))



  "RcEventsAPI" should {
    "return a newly created event" when {
      "user create event" in {
        (eventService.create _)
          .expects(createEventRequest.event)
          .returns(Future.successful(createEventResponse.event))

        Post("/events/v1/create", createEventRequest) ~> routes ~> check {
          assert(status == StatusCodes.OK)
          assert(entityAs[CreateEventResponse] == createEventResponse)
        }
      }
    }

    "find existed event" when {
      "user search event" in {
        (eventService.find _)
          .expects(findEventRequest.event)
          .returns(Future.successful(findEventResponse.eventOpt))

        Post("/events/v1/find", findEventRequest) ~> routes ~> check {
          assert(status == StatusCodes.OK)
          assert(entityAs[FindEventResponse] == findEventResponse)
        }
      }
    }

    "delete existed event" when {
      "user delete event" in {
        (eventService.remove _)
          .expects(deleteEventRequest.event)
          .returns(Future.successful(deleteEventResponse.event))

        Post("/events/v1/delete", deleteEventRequest) ~> routes ~> check {
          assert(status == StatusCodes.OK)
          assert(entityAs[DeleteEventResponse] == deleteEventResponse)
        }
      }
    }

    "update existed event" when {
      "user update event" in {
        (eventService.updateEvent _)
          .expects(updateEventRequest.oldEvent,updateEventRequest.newEvent)
          .returns(Future.successful(updateEventResponse.newEvent))

        Post("/events/v1/update", updateEventRequest) ~> routes ~> check {
          assert(status == StatusCodes.OK)
          assert(entityAs[UpdateEventResponse] == updateEventResponse)
        }
      }
    }

    "fail with 500" when {
      "user's attempt to create event which is already exist" in {
        (eventService.create _)
          .expects(*)
          .returns(Future.failed(new RuntimeException))

        Post("/events/v1/create", createEventRequest) ~> routes ~> check {
          assert(status == StatusCodes.InternalServerError)
        }
      }

      "user's attempt to delete a non-existent event" in {
        (eventService.remove _)
          .expects(*)
          .returns(Future.failed(new RuntimeException))

        Post("/events/v1/delete", deleteEventRequest) ~> routes ~> check {
          assert(status == StatusCodes.InternalServerError)
        }
      }

      "user's attempt to update a non-existent event" in {
        (eventService.updateEvent _)
          .expects(*,*)
          .returns(Future.failed(new RuntimeException))

        Post("/events/v1/update", updateEventRequest) ~> routes ~> check {
          assert(status == StatusCodes.InternalServerError)
        }
      }
    }
  }
}
