name := "NoBreath"

version := "1.0.0"

scalaVersion := "2.9.1"

seq(ProguardPlugin.proguardSettings :_*)

proguardOptions += keepMain("jp.gr.java_conf.frontier.nobreath.Main")


libraryDependencies ++= Seq(
                "org.specs2" %% "specs2" % "1.8.2" % "test",
                "org.specs2" %% "specs2-scalaz-core" % "6.0.1" % "test",
                "junit" % "junit" % "4.9" % "test"
)

libraryDependencies <+= scalaVersion { "org.scala-lang" % "scala-swing" % _ }

