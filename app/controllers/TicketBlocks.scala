package controllers

import controllers.responses.{ErrorResponse, SuccessResponse}
import models.TicketBlock
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by sromic on 25/06/15.
 */
object TicketBlocks extends Controller {

  def list = Action.async { reguest =>
    val ticketBlocks: Future[Seq[TicketBlock]] = TicketBlock.list

    ticketBlocks.map { blockList =>
      Ok(Json.toJson(blockList))
    }
  }

  def getById(ticketBlockId: Long) = Action.async { request =>
    val ticketBlockFuture: Future[Option[TicketBlock]] = TicketBlock.getById(ticketBlockId)

    ticketBlockFuture.map { ticketBlock =>
      ticketBlock.fold {
        NotFound(Json.toJson(ErrorResponse(NOT_FOUND, "No ticket block found")))
      } { tb =>
        Ok(Json.toJson(SuccessResponse(tb)))
      }
    }
  }

  def create = Action.async(parse.json) { request =>
    val incomingBody = request.body.validate[TicketBlock]

    incomingBody.fold(error => {
      val errorMessage = s"Invalid JSON: ${error}"
      val response = ErrorResponse(ErrorResponse.INVALID_JSON, errorMessage)
      Future.successful(BadRequest(Json.toJson(response)))
    }, { ticketBlock =>
      val createdBlockFuture: Future[TicketBlock] = TicketBlock.create(ticketBlock)

      createdBlockFuture.map { createdBlock =>
        Created(Json.toJson(SuccessResponse(createdBlock)))
      }
    })
  }

}
