lazy val root = (project in file(".")).
  settings(
    name := "hello",
    version := "1.0",
    scalaVersion := "2.11.7"
  )

resolvers += "Twitter Maven repo" at "https://maven.twttr.com/"

libraryDependencies += "org.apache.thrift" % "libthrift" % "0.9.2"
libraryDependencies += "com.twitter" %% "finagle-thrift" % "6.27.0"
libraryDependencies += "com.twitter" %% "scrooge-core" % "3.3.2"
libraryDependencies += "com.twitter" %% "twitter-server" % "1.12.0"
libraryDependencies += "com.twitter" %% "finagle-stats" % "6.27.0"
libraryDependencies += "com.twitter" %% "finagle-zipkin" % "6.20.0"




