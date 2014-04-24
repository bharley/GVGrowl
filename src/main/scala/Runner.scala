package com.blakeharley.gvgrowl

import scala.concurrent.duration._

import akka.actor.Actor
import akka.event.Logging

/**
 * Listens and acts upon new messages.
 *
 * @param voice The Google Voice API to draw from
 */
class Runner(private val voice: GoogleVoice, private val growler: Growler) extends Actor {
	import context.dispatcher

	val CheckMessages = "checkMessages"

	private val log = Logging(context.system, this)

	private val time = 10.seconds

	private val tick = context.system.scheduler.schedule(time, time, self, CheckMessages)

	/**
	 * Handles messages received from the Akka actor system.
	 *
	 * @return Necessary case stuff
	 */
	override def receive = {
		case CheckMessages => {
			log.info("Checking for new messages...")

			val messages = voice.newMessages
			// Do we have a new message?
			if (messages.length > 0)
			{
				log.info("New messages!")

				messages.foreach(growler.newTextNotification)
			}
			else
			{
				log.info("No new messages")
			}
		}
		case _ => log.error("Unknown message type")
	}

	/**
	 * Stops this actor from actoring.
	 */
	def stop() = {
		tick.cancel()
	}
}
