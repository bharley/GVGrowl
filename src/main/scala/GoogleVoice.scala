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
	 * The Google Voice API instance.
	 */
	private lazy val voice = new Voice(username, password)

	/**
	 * Gets all of the SMS messages available on the first page and returns them in
	 * chronological order.
	 *
	 * @return All messages on the SMS messages page
	 */
	def messages: List[SMS] = {
		smsData(voice.get("https://www.google.com/voice/request/messages")).sorted
	}

	/**
	 * Extracts useful SMS data out of a Json string.
	 *
	 * @param rawJson The JSON response to work with
	 * @return
	 */
	private def smsData(rawJson: String): List[SMS] = {
		class CC[T] { def unapply(a:Any):Option[T] = Some(a.asInstanceOf[T]) }
		object M extends CC[Map[String, Any]]
		object L extends CC[List[Any]]
		object S extends CC[String]

		for {
			Some(M(map)) <- List(JSON.parseFull(rawJson))
			L(messageLists) = map("messageList")
			M(messageGroup) <- messageLists
			L(messages) = messageGroup("children")
			M(sms) <- messages
			S(id) = sms("id")
			S(message) = sms("message")
			S(from) = sms("phoneNumber")
			S(timestamp) = sms("startTime")
		} yield {
			SMS(id, from, message, new DateTime(timestamp.toLong))
		}
	}
}

/**
 * Simple container for SMS data.
 *
 * @param id The message ID
 * @param from The phone number of the person who sent this
 * @param message The message contents
 * @param datetime The time this message was recieved
 */
case class SMS(id: String, from: String, message: String, datetime: DateTime) extends Ordered[SMS] {
	def compare(that: SMS) = that.datetime.compare(datetime)
}