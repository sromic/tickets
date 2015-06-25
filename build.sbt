name := "tickets"

version := "1.0"

lazy val `tickets` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(cache, ws,
  "org.webjars" % "jquery" % "2.1.3",
  specs2 % Test,
  "com.typesafe.play" %% "play-slick-evolutions" % "1.0.0",
  "com.typesafe.play" %% "play-slick" % "1.0.0",
  "com.h2database" % "h2" % "1.4.187"
)

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"