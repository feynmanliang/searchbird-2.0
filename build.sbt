lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "com.feynmanliang",
  scalaVersion := "2.11.7"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "searchbird-2.0",
    mainClass in Compile := Some("com.feynmanliang.searchbird.Main")
  )

resolvers += "Twitter Maven repo" at "https://maven.twttr.com/"

libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value
libraryDependencies += "org.apache.thrift" % "libthrift" % "0.9.2"
libraryDependencies += "com.twitter" %% "finagle-thrift" % "6.27.0"
libraryDependencies += "com.twitter" %% "scrooge-core" % "3.20.0"
libraryDependencies += "com.twitter" %% "twitter-server" % "1.12.0"
libraryDependencies += "com.twitter" %% "finagle-stats" % "6.27.0"
libraryDependencies += "com.twitter" %% "finagle-zipkin" % "6.27.0"



