name := "GVGrowl"

version := "0.1"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "1.0.0"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.2"

libraryDependencies += "org.streum" %% "configrity-core" % "1.0.0"

mainClass := Some("com.blakeharley.gvgrowl.GVGrowl")

seq(appbundle.settings: _*)

appbundle.javaVersion := "1.6+"

appbundle.screenMenu := false

appbundle.name := "GVGrowl"

appbundle.normalizedName := "gvgrowl"

appbundle.organization := "com.blakeharley"

appbundle.version := "0.1"

appbundle.icon := Some(file("src") / "main" / "resources" / "icon.png")
