package com.epam.random_coffee.events.services

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

  private val event = Event("test_event")
  private val newEvent = Event("test_event_new")

  "EventService" should {

    "create an event" when {
      "event doesn't exist" in {
        (repo.find _).expects(event).returns(Future.successful(None))

        (repo.save _).expects(*).returns(Future.unit)

        service.create(event).map(_ => succeed)
      }
    }

    "delete an event" when {
      "event exists" in {
        (repo.find _).expects(event).returns(Future.successful(Some(event)))

        (repo.delete _).expects(*).returns(Future.unit)

        service.remove(event).map(_ => succeed)
      }
    }

    "update an event" when {
      "event exists" in {
        (repo.find _).expects(event).returns(Future.successful(Some(event)))

        (repo.refresh _).expects(*,*).returns(Future.unit)

        service.updateEvent(event, newEvent).map(_ => succeed)
      }
    }

    "find an event" when {
      "there is an event in repo" in {
        (repo.find _).expects(event).returns(Future.successful(Some(event)))

        service.find(event).map(testEvent => assert(testEvent.contains(event)))
      }

      "there is no user in repo" in {
        (repo.find _).expects(event).returns(Future.successful(None))

        service.find(event).map(testUser => assert(testUser.isEmpty))
      }
    }

    "fail to create an event" when {

      "such event already exists" in {
        (repo.find _).expects(event).returns(Future.successful(Some(event)))

        service
          .create(event)
          .failed
          .map(_.getMessage)
          .map(msg => assert(msg == s"Event ${event.eventName} already exists"))
      }
    }

    "fail to delete an event" when {

      "such event doesn't exists" in {
        (repo.find _).expects(event).returns(Future.successful(None))

        service
          .remove(event)
          .failed
          .map(_.getMessage)
          .map(msg => assert(msg == s"Event ${event.eventName} doesn't exist"))
      }
    }

    "fail to update an event" when {

      "such event doesn't exists" in {
        (repo.find _).expects(event).returns(Future.successful(None))

        service
          .updateEvent(event,newEvent)
          .failed
          .map(_.getMessage)
          .map(msg => assert(msg == s"Event ${event.eventName} doesn't exist"))
      }
    }
  }
}
