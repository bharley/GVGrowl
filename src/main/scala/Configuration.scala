package com.blakeharley.gvgrowl;

import org.streum.configrity;

/**
 * Handles loading relevant information out of our configuration
 * storage and writing back to it.
 */
class Configuration {
	private val configPath = getClass.getResource("/").getPath + "settings.conf"

	private val ConfUsername = "username"
	private val ConfPassword = "password"
	private val ConfAuthToken = "authToken"

	/**
	 * Ensures that the configuration file exists before trying to load it.
	 */
	private var config: configrity.Configuration = {
		import java.io.File;
		val file = new File(configPath)
		if (!file.exists) {
			file.createNewFile()
		}

		configrity.Configuration.load(configPath)
	}

	/**
	 * @return The password from the current config
	 */
	def username: Option[String] = {
		try {
			Some(config[String](ConfUsername))
		} catch {
			case e: java.util.NoSuchElementException => None
		}
	}

	/**
	 * @return The password from the current config
	 */
	def password: Option[String] = {
		try {
			Some(config[String](ConfPassword))
		} catch {
			case e: java.util.NoSuchElementException => None
		}
	}

	/**
	 * @return The auth token from the current config
	 */
	def authToken: Option[String] = {
		try {
			Some(config[String](ConfAuthToken))
		} catch {
			case e: java.util.NoSuchElementException => None
		}
	}

	/**
	 * Checks to see if the configurations we have right now is enough to
	 * work with
	 *
	 * @return Whether we can work
	 */
	def isValid: Boolean = username.isDefined && password.isDefined

	/**
	 * Updates the user's Google credentials.
	 *
	 * @param username The username
	 * @param password The password
	 */
	def saveCredentials(username: String, password: String) = {
		config = config.set(ConfUsername, username).set(ConfPassword, password)
		save()
	}

	/**
	 * Saves a Google auth token to the settings storage.
	 *
	 * @param authToken The auth token to save
	 */
	def saveAuthToken(authToken: String) = {
		config = config.set(ConfAuthToken, authToken)
		save()
	}

	/**
	 * Saves the current configuration to the filesystem.
	 */
	private def save() = config.save(configPath)
}
