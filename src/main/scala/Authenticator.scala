package com.blakeharley.gvgrowl

import com.techventus.server.voice.Voice

/**
 * Handles authenticating our Google sessions.
 *
 * @param config The config to read from and write back to
 */
class Authenticator(private val config: Configuration) {

	def username: Option[String] = config.username

	def password: Option[String] = config.password

	def authToken: Option[String] = config.authToken

	/**
	 * Gets an instance of the Google Voice API using whatever
	 * credentials we have.
	 */
	lazy val voice: Voice = {
		// todo: Ask the user for stuff instead of this crappy route
		if (!config.isValid) {
			throw new Exception("Invalid configuration")
		}

		val key: String = authToken match {
			case Some(token) => token
			case None => null
		}

		val voice = new Voice(username.get, password.get, key, "GoogleVoice", false)
		if (!voice.isLoggedIn) {
			voice.login()
		}

		// Write the auth key back
		config.saveAuthToken(voice.getAuthToken)

		voice
	}
}
