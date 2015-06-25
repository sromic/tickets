package controllers.responses

import play.api.libs.json._

/**
 * Created by sromic on 25/06/15.
 */

case class ErrorResult(status: Int, message: String)

object ErrorResult {
  implicit val format: Format[ErrorResult] = Json.format[ErrorResult]
}

sealed case class EndPointResponse(
                                    result: String,
                                    response: JsValue,
                                    error: Option[ErrorResult]
                                    )

object EndPointResponse {
  implicit val format: Format[EndPointResponse] = Json.format[EndPointResponse]
}

object ErrorResponse {
  val INVALID_JSON = 1000
  val NOT_ENOUGH_TICKETS = 1001
  val TICKET_BLOCK_UNAVAILABLE = 1002

  def apply(status: Int, message: String) = {
    EndPointResponse("ko", JsNull, Option(ErrorResult(status, message)))
  }
}

object SuccessResponse {
  def apply[A](successResponse: A)(implicit w: Writes[A]) = {
    EndPointResponse("ok", Json.toJson(successResponse), None)
  }
}