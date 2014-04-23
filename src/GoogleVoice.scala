package com.blakeharley.gvgrowl

import scala.util.parsing.json._
import com.techventus.server.voice.Voice

/**
 * Handles interacting with the Google voice services. Essentially serves as a thin wrapper
 * around the Google Voice Java API.
 *
 * @param username Google Voice username
 * @param password Google Voice password
 */
class GoogleVoice(username: String, password: String) {
	private lazy val voice = new Voice(username, password)

	/**
	 * Gets all of the SMS messages available on the first page and returns them in
	 * chronological order.
	 *
	 * @return All messages on the SMS messages page
	 */
	def messages: String = {
		println(JSON.parseFull(voice.get("https://www.google.com/voice/request/messages")))
		""
	}
}
