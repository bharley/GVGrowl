package com.blakeharley.gvgrowl

import com.sun.jna._

/**
 * Wrapper around the Growl API to provide notifications.
 */
class Growler {
	/**
	 * Sends a notification about getting a new text message.
	 *
	 * @param sms The text message
	 */
	def newTextNotification(sms: SMS) = {
		NSNotifications.sendNotification(sms.from, "", sms.message, 0)
	}
}

/**
 * Wrapper for the native notification center calls.
 */
object NSNotifications {
	Native.register("/NsUserNotificationsBridge.dylib")
	@native def sendNotification(title: String, subtitle: String, text: String, timeoffset: Int): Int
}
