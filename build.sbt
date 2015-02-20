name := "AnomalyDetection"

version := "1.0"

scalaVersion := "2.11.1"

scalacOptions ++= Seq("-unchecked", "-deprecation")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

val akkaV = "2.3.6"

libraryDependencies ++= Seq(
  "joda-time"               % "joda-time"                 % "1.6",
	"org.scalatest"           %% "scalatest"                % "2.2.0"       % "test",
	"org.specs2"              %% "specs2"                   % "2.3.12"      % "test",
	"org.scala-lang.modules"  %% "scala-xml"                % "1.0.2",
  "org.scala-lang.modules"  %% "scala-parser-combinators" % "1.0.1",
	"org.json4s"              %% "json4s-native"            % "3.2.10",
  "junit"                   % "junit"                     % "4.7"         % "test",
  "com.typesafe.akka"       %%  "akka-actor"              % akkaV,
  "com.typesafe.akka"       %%  "akka-testkit"            % akkaV   % "test",
  "org.apache.spark"        %% "spark-core"               % "1.2.0",
  "org.apache.spark"        %% "spark-streaming"          % "1.2.0",
  "ch.qos.logback"          %  "logback-classic"          % "1.1.2"
)
    