sbtPlugin		:= true

name			:= "xsbt-concat"
organization	:= "de.djini"
version			:= "0.3.0"

scalacOptions	++= Seq(
	"-deprecation",
	"-unchecked",
	// "-language:implicitConversions",
	// "-language:existentials",
	// "-language:higherKinds",
	// "-language:reflectiveCalls",
	// "-language:dynamics",
	// "-language:postfixOps",
	// "-language:experimental.macros"
	"-feature",
	"-Xfatal-warnings"
)

conflictManager	:= ConflictManager.strict
addSbtPlugin("de.djini" % "xsbt-util"	% "0.4.0")
addSbtPlugin("de.djini" % "xsbt-webapp"	% "1.5.0")
