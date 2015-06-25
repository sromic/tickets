package controllers

import controllers.responses.ErrorResponse
import models._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

/**
 * Created by sromic on 25/06/15.
 */
object Events extends Controller {

  def list = Action.async { request =>
    val eventsFuture: Future[Seq[Event]] = Event.list

    val response = eventsFuture.map { events =>
      Ok(Json.toJson(responses.SuccessResponse(events)))
    }

    response
  }

  def getById(eventId: Long) = Action.async { request =>
    val eventFuture: Future[Option[Event]] = Event.getById(eventId)

    eventFuture.map { event =>
      event.fold {
        NotFound(Json.toJson(responses.ErrorResult(NOT_FOUND, "No event found")))
      } { e =>
        Ok(Json.toJson(responses.SuccessResponse(e)))
      }
    }
  }

  def create = Action.async(parse.json) { request =>
    val incomingEvent = request.body.validate[Event]

    incomingEvent.fold(error => {
      val errorMessage = s"Invalid JSON: ${error}"
      val response = ErrorResponse(ErrorResponse.INVALID_JSON, errorMessage)

      Future.successful(BadRequest(Json.toJson(response)))
    }, { event =>
      val createdEventFuture: Future[Event] = Event.create(event)

      createdEventFuture.map { createdEvent =>
        Created(Json.toJson(responses.SuccessResponse(createdEvent)))
      }
    })
  }


}
