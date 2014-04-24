package com.blakeharley.gvgrowl

import com.techventus.server.voice.Voice
import org.streum.configrity._

class Authenticator {
	private val configPath = getClass.getResource("/settings.conf").getPath
	lazy val config = Configuration.load(configPath)

	lazy val username: Option[String] = {
		try {
			Some(config[String]("username"))
		} catch {
			case e: java.util.NoSuchElementException => None
		}
	}

	lazy val password: Option[String] = {
		try {
			Some(config[String]("password"))
		} catch {
			case e: java.util.NoSuchElementException => None
		}
	}

	lazy val authToken: Option[String] = {
		try {
			Some(config[String]("authToken"))
		} catch {
			case e: java.util.NoSuchElementException => None
		}
	}

	lazy val voice: Voice = {
		// todo: Ask the user for stuff instead of this crappy route
		if (username.isEmpty || password.isEmpty) {
			throw new Exception("Username or password not defined")
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
		config.set("authToken", voice.getAuthToken).save(configPath)

		voice
	}
}
