package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
 * Created by sromic on 25/06/15.
 */
object Tickets extends Controller {

  case class AvailabilityResponse(result: String, ticketQuantity: Option[Long])

  /**
   * implicit format to tell Play how to format Json from AvailabilityResponse object
   * case class support out of the bow Json reads and writes
   * to return object serialized as Json call toJson of object
   */
  object AvailabilityResponse {
    implicit val responseFormat = Json.format[AvailabilityResponse]
  }

  def ticketsAvailable = Action { request =>
    val availableTickets = 1000
    val result = AvailabilityResponse("ok", Option(availableTickets))

    Ok(Json.toJson(result))
  }
}
