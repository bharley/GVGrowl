package com.blakeharley.gvgrowl

import javax.swing

import akka.actor.{Props, ActorSystem}

object GVGrowl extends swing.JFrame {
	val menu = new swing.JMenuBar

	val fileMenu = new swing.JMenuItem()
	fileMenu.setText("File")
	fileMenu.setMnemonic('f')
	menu.add(fileMenu)

	setJMenuBar(menu)


	def main(args: Array[String]) {
		val system = ActorSystem("GVGrowl")
		val actor = system.actorOf(Props(classOf[Runner], voice, new Growler))
		Thread.sleep(100000L)
	}

	private lazy val config = new Configuration
	private lazy val auth = new Authenticator(config)
	private lazy val voice = new GoogleVoice(auth.voice)
}
