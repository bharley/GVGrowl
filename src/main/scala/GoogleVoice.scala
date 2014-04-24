package com.blakeharley.gvgrowl

import scala.util.parsing.json._

import com.github.nscala_time.time.Imports._
import com.techventus.server.voice.Voice

/**
 * Handles interacting with the Google voice services. Essentially serves as a thin wrapper
 * around the Google Voice Java API.
 *
 * @param username Google Voice username
 * @param password Google Voice password
 */
class GoogleVoice(username: String, password: String) {

	/**
	 * Contains a pointer to the last new message
	 */
	private var lastMessageId = messages(0).id

	/**
	 * The Google Voice API instance.
	 */
	private lazy val voice = new Voice(username, password, null, false)

	/**
	 * Tracking variable for the following filter.
	 */
	private var foundBoundary = false

	/**
	 * Filters new messages out. Apparently I'm too dumb to be able to stuff this
	 * logic in the filter call below... not sure why.
	 *
	 * @param sms The message to filter
	 * @return Whether or not this message belongs
	 */
	def filterPredicate(sms: SMS): Boolean = {
		if (sms.id.equals(lastMessageId)) foundBoundary = true
		!foundBoundary
	}

	/**
	 * Fetches all new messages in reverse chronological order.
	 *
	 * @return Any new messages
	 */
	def newMessages: List[SMS] = {
		val messages = this.messages
		var newMessages: List[SMS] = List()

		if (!lastMessageId.equals(messages(0).id)) {
			foundBoundary = false
			newMessages = messages.filter(filterPredicate).reverse
			lastMessageId = messages(0).id
		}

		newMessages
	}

	/**
	 * Gets the contact's name if it exists.
	 *
	 * @param number The phone number to look up
	 * @return The contact's name, otherwise the phone number is returned
	 */
	def getContactName(number: String): String = {
		contacts.get(number) match {
			case None => number
			case Some(name) => name
		}
	}

	/**
	 * Contact information.
	 *
	 * note: This probably needs to be reloaded when run for long periods of time
	 */
	lazy val contacts = contactsData(voice.get("https://www.google.com/voice/request/contacts"))

	/**
	  * Gets all of the SMS messages available on the first page and returns them in
	 * chronological order. All messages from the current user are stripped out.
	 *
	 * @return All messages on the SMS messages page
	 */
	def messages: List[SMS] = {
		smsData(voice.get("https://www.google.com/voice/request/messages")).filter(_.msgType == 10).sorted
	}

	// Silly JSON typedefs
	class CC[T] { def unapply(a:Any):Option[T] = Some(a.asInstanceOf[T]) }
	object M extends CC[Map[String, Any]]
	object T extends CC[(String, Map[String, Any])]
	object L extends CC[List[Any]]
	object S extends CC[String]
	object D extends CC[Double]

	import collection.breakOut

	/**
	 * Extracts useful contacts data out of a Json string.
	 *
	 * @param rawJson The JSON response to work with
	 * @return A map of contacts to their phone number
	 */
	private def contactsData(rawJson: String): Map[String, String] = (for {
		Some(M(map)) <- List(JSON.parseFull(rawJson))
		M(contacts) = map("contactPhones")
		T(contact) <- contacts
		S(phone) = contact._1
		M(info) = contact._2
		Some(S(name)) = info.get("name")
	} yield {
		phone -> name
	})(breakOut)

	/**
	 * Extracts useful SMS data out of a Json string.
	 *
	 * @param rawJson The JSON response to work with
	 * @return The list of SMS messages
	 */
	private def smsData(rawJson: String): List[SMS] = for {
		Some(M(map)) <- List(JSON.parseFull(rawJson))
		L(messageLists) = map("messageList")
		M(messageGroup) <- messageLists
		L(messages) = messageGroup("children")
		M(sms) <- messages
		S(id) = sms("id")
	  D(msgType) = sms("type")
		S(message) = sms("message")
		S(from) = sms("phoneNumber")
		S(timestamp) = sms("startTime")
	} yield {
		SMS(id, msgType.toInt, getContactName(from), message, new DateTime(timestamp.toLong))
	}
}

/**
 * Simple container for SMS data.
 *
 * @param id The message ID
 * @param msgType The message type
 * @param from The phone number of the person who sent this
 * @param message The message contents
 * @param datetime The time this message was recieved
 */
case class SMS(id: String, msgType: Int, from: String, message: String, datetime: DateTime) extends Ordered[SMS] {
	def compare(that: SMS) = that.datetime.compare(datetime)
}