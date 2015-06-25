package controllers

import controllers.responses._
import models.{Order, TicketBlock}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.{Controller, _}

import scala.concurrent.Future

/**
 * Created by sromic on 25/06/15.
 */
object Orders extends Controller {

  def list = Action.async { request =>
    val orders = Order.list
    orders.map { o =>
      Ok(Json.toJson(SuccessResponse(o)))
    }
  }

  def getByID(orderID: Long) = Action.async { request =>
    val orderFuture = Order.getById(orderID)

    orderFuture.map { order =>
      order.fold {
        NotFound(Json.toJson(ErrorResponse(NOT_FOUND, "No order found")))
      } { o =>
        Ok(Json.toJson(SuccessResponse(o)))
      }
    }
  }

  def create = Action.async(parse.json) { request =>
    val incomingBody = request.body.validate[Order]

    incomingBody.fold(error => {
      val errorMessage = s"Invalid JSON: ${error}"
      val response = ErrorResponse(ErrorResponse.INVALID_JSON, errorMessage)
      Future.successful(BadRequest(Json.toJson(response)))
    }, { order =>

      val availFuture = TicketBlock.availability(order.ticketBlockId)

      availFuture.flatMap { availability =>
        if (availability >= order.ticketQuantity) {
          // save order and get a copy back
          val createdOrder = Order.create(order)

          createdOrder.map { co =>
            Created(Json.toJson(SuccessResponse(co)))
          }
        } else {
          val responseMessage = "There are not enough tickets remaining to complete this order." +
            s" Quantity Remaining: ${availability}"

          val response = ErrorResponse(
            ErrorResponse.NOT_ENOUGH_TICKETS,
            responseMessage)

          Future.successful(BadRequest(Json.toJson(response)))
        }
      }
    })
  }
}
