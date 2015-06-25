package models

import models.SlickMapping.jodaDateTimeMapping
import org.joda.time.DateTime
import play.api.Play.current
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.{Format, Json}
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
 * Created by sromic on 25/06/15.
 */

case class Event(
                  id: Option[Long],
                  name: String,
                  start: DateTime,
                  end: DateTime,
                  address: String,
                  city: String,
                  state: String,
                  country: String
                  )

object Event {
  implicit val format: Format[Event] = Json.format[Event]

  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](current)

  import dbConfig._
  import dbConfig.driver.api._

  class EventsTable(tag: Tag) extends Table[Event](tag, "EVENTS") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME")

    def start = column[DateTime]("START")

    def end = column[DateTime]("END")

    def address = column[String]("ADDRESS")

    def city = column[String]("CITY")

    def state = column[String]("STATE")

    def country = column[String]("COUNTRY")

    def * = (id.?, name, start, end, address, city, state, country) <>
      ((Event.apply _).tupled, Event.unapply)
  }

  val table = TableQuery[EventsTable]

  def list: Future[Seq[Event]] = {
    val eventList = table.result

    db.run(eventList)
  }

  def getById(eventId: Long): Future[Option[Event]] = {
    val eventById = table.filter { f =>
      f.id === eventId
    }.result.headOption

    db.run(eventById)
  }

  def create(newEvent: Event): Future[Event] = {
    val insertion = (table returning table.map(_.id)) += newEvent

    val insertedIdFuture = db.run(insertion)

    val createdCopy: Future[Event] = insertedIdFuture.map { resultId =>
      newEvent.copy(id = Option(resultId))
    }

    createdCopy
  }
}

case class TicketBlock(
                        id: Option[Long],
                        eventId: Long,
                        name: String,
                        productCode: String,
                        price: BigDecimal,
                        initialSize: Int,
                        saleStart: DateTime,
                        saleEnd: DateTime
                        )

object TicketBlock {
  implicit val format: Format[TicketBlock] = Json.format[TicketBlock]

  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](current)

  import dbConfig._
  import dbConfig.driver.api._

  class TicketBlocksTable(tag: Tag) extends Table[TicketBlock](tag, "TICKET_BLOCKS") {
    def id = column[Long]("EVENT_ID")

    def eventId = column[Long]("EVENT_ID")

    def name = column[String]("NAME")

    def productCode = column[String]("PRODUCT_CODE")

    def price = column[BigDecimal]("PRICE")

    def initialSize = column[Int]("INITIAL_SIZE")

    def saleStart = column[DateTime]("SALE_START")

    def saleEnd = column[DateTime]("SALE_END")

    def event = foreignKey("TB_EVENT", eventId, Event.table)(_.id)

    def * = (id.?, eventId, name, productCode, price, initialSize,
      saleStart, saleEnd) <>
      ((TicketBlock.apply _).tupled, TicketBlock.unapply)
  }

  val table = TableQuery[TicketBlocksTable]

  def list: Future[Seq[TicketBlock]] = {
    val blockList = table.result
    db.run(blockList)
  }

  def getById(blockId: Long): Future[Option[TicketBlock]] = {
    val blockById = table.filter { f =>
      f.id === blockId
    }.result.headOption

    db.run(blockById)
  }

  def create(newTicketBlock: TicketBlock): Future[TicketBlock] = {
    val insertion = (table returning table.map(_.id)) += newTicketBlock

    db.run(insertion).map { resultId =>
      newTicketBlock.copy(id = Option(resultId))
    }
  }

  def availability(ticketBlockID: Long): Future[Int] = {
    val orders = for {
      o <- Order.table if o.ticketBlockID === ticketBlockID
    } yield o.ticketQuantity

    val quantityLeft = table.filter {
      _.id === ticketBlockID
    }.map {
      tb => tb.initialSize - orders.sum
    }

    val queryResult = db.run(quantityLeft.result.headOption)

    queryResult.map {
      _.flatten.getOrElse(0)
    }

  }

}

case class Order(id: Option[Long],
                 ticketBlockId: Long,
                 customerName: String,
                 customerEmail: String,
                 ticketQuantity: Int,
                 timestamp: Option[DateTime])

object Order {
  implicit val format: Format[Order] = Json.format[Order]

  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](current)

  import dbConfig._
  import dbConfig.driver.api._

  class OrdersTable(tag: Tag) extends Table[Order](tag, "ORDERS") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def ticketBlockID = column[Long]("TICKET_BLOCK_ID")

    def customerName = column[String]("CUSTOMER_NAME")

    def customerEmail = column[String]("CUSTOMER_EMAIL")

    def ticketQuantity = column[Int]("TICKET_QUANTITY")

    def timestamp = column[DateTime]("TIMESTAMP")

    def ticketBlock = foreignKey("O_TICKETBLOCK",
      ticketBlockID, TicketBlock.table)(_.id)

    def * = (id.?, ticketBlockID, customerName, customerEmail,
      ticketQuantity, timestamp.?) <>
      ((Order.apply _).tupled, Order.unapply)
  }

  val table = TableQuery[OrdersTable]

  def list: Future[Seq[Order]] = {
    db.run(table.result)
  }

  def getById(orderId: Long): Future[Option[Order]] = {
    db.run {
      table.filter { f =>
        f.id === orderId
      }.result.headOption
    }
  }

  def create(newOrder: Order): Future[Order] = {
    val nowStamp = new DateTime()
    val withTimestamp = newOrder.copy(timestamp = Option(nowStamp))

    val insertion = (table returning table.map(_.id)) += withTimestamp

    db.run(insertion).map { resultID =>
      withTimestamp.copy(id = Option(resultID))
    }
  }
}


