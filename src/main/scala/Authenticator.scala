package com.blakeharley.gvgrowl

import java.security.KeyStore._

class Authenticator {

}

case class Identity(username: String, password: String, authKey: String) extends Entry {
	override def getAttributes: Set[IdentityAttribute] =
		Set(IdentityAttribute("username", username),
			IdentityAttribute("password", password),
			IdentityAttribute("authKey", authKey))

	/**
	 * Simple class for storing a key-value pair
	 * @param name The attribute name
	 * @param value The attribute value
	 */
	case class IdentityAttribute(name: String, value: String) extends Entry.Attribute {
		override def getName: String = name
		override def getValue: String = value
	}
}