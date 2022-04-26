package com.epam.random_coffee.events.services

import com.epam.random_coffee.events.api.request.{ CreateEventRequest, UpdateEventRequest }
import com.epam.random_coffee.events.api.response.EventResponse
import com.epam.random_coffee.events.model.Event
import com.epam.random_coffee.events.repo.EventRepository
import com.epam.random_coffee.events.services.impl.EventServiceImpl
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.OneInstancePerTest
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.Future

class EventServiceImplSpec extends AsyncWordSpec with AsyncMockFactory with OneInstancePerTest {
  private val repo = mock[EventRepository]
  private val service = new EventServiceImpl(repo)

  private val id = 1
  private val event = Event(id, "created_event")
  private val createRequest = CreateEventRequest("created_event")
  private val updateRequest = UpdateEventRequest("updated_event")
  private val eventResponseForCreate = EventResponse(id, "created_event")

  "EventService" should {

    "create an event" when {
      "event doesn't exist" in {
        (repo.findByName _).expects(createRequest.eventName).returns(Future.successful(None))

        (repo.save _).expects(createRequest.eventName).returns(Future.unit)

        (repo.findByName _).expects(createRequest.eventName).returns(Future.successful(Some(eventResponseForCreate)))

        service.create(createRequest).map(_ => succeed)
      }
    }

    "delete an event" when {
      "event exists" in {
        (repo.findById _).expects(id).returns(Future.successful(Some(event)))

        (repo.delete _).expects(*).returns(Future.unit)

        service.delete(id).map(_ => succeed)
      }
    }

    "update an event" when {
      "event exists" in {
        (repo.findById _).expects(id).returns(Future.successful(Some(event)))

        (repo.refresh _).expects(*, *).returns(Future.unit)

        service.updateEvent(id, updateRequest).map(_ => succeed)
      }
    }

    "find an event" when {
      "there is an event in repo" in {
        (repo.findById _).expects(id).returns(Future.successful(Some(event)))

        service.find(id).map(testEvent => assert(testEvent.contains(event)))
      }
    }

    "fail to create an event" when {

      "such event already exists" in {
        (repo.findByName _).expects("created_event").returns(Future.successful(Some(eventResponseForCreate)))

        service
          .create(createRequest)
          .failed
          .map(_.getMessage)
          .map(msg => assert(msg == s"Event ${createRequest.eventName} already exists"))
      }
    }

    "fail to delete an event" when {

      "such event doesn't exists" in {
        (repo.findById _).expects(id).returns(Future.successful(None))

        service.delete(id).failed.map(_.getMessage).map(msg => assert(msg == s"Event with id № $id doesn't exist"))
      }
    }

    "fail to update an event" when {

      "such event doesn't exists" in {
        (repo.findById _).expects(id).returns(Future.successful(None))

        service
          .updateEvent(id, updateRequest)
          .failed
          .map(_.getMessage)
          .map(msg => assert(msg == s"Event with id № $id doesn't exist"))
      }
    }
  }
}
