package com.blakeharley.gvgrowl

object GVGrowl {
	def main(args: Array[String]) {
		runner(new GoogleVoice("<username>", "<password>"))
	}

	def runner(voice: GoogleVoice) {
		// testing
		voice.messages
	}
}
