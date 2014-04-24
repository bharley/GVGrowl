package com.blakeharley.gvgrowl

import akka.actor.{Props, ActorSystem}

object GVGrowl {
	def main(args: Array[String]) {
		val system = ActorSystem("GVGrowl")
		val actor = system.actorOf(Props(classOf[Runner], voice, new Growler))
		Thread.sleep(100000L)
	}

	private lazy val voice = new GoogleVoice("username", "password")
}
