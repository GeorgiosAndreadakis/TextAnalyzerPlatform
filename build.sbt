import sbt.Keys.libraryDependencies

organization := "org.textanalyserplatform"
name := "Text Analyser Platform"
version := "0.1.0-SNAPSHOT"
scalaVersion := "2.11.8"

resolvers += "Maven Central" at "http://central.maven.org/maven2"
resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases"

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")

lazy val commonSettings = Seq(
  organization := "org.textanalyserplatform",
  name := "Text Analyser Platform",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.11.8"
)

lazy val defaultLibs = Seq(
  "junit" % "junit" % "4.12" % Test,
  "com.novocode" % "junit-interface" % "0.11" % Test,
  "org.scalactic" %% "scalactic" % "3.0.1" % Test,
  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)


def ScalaTestProject(name: String): Project = {
  Project(name, file(name))
}

lazy val root = (project in file("."))
  .aggregate(acceptancetests, application, domain)
  .settings(
    commonSettings
  )

lazy val acceptancetests = (
  ScalaTestProject("acceptancetests")
    settings(
    libraryDependencies ++= defaultLibs,
    libraryDependencies += "info.cukes" %% "cucumber-scala" % "1.2.5" % Test,
    libraryDependencies += "info.cukes" % "cucumber-junit" % "1.2.5" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % Test
  )
  ) dependsOn application

lazy val application = (
  ScalaTestProject("application")
    settings (
    )
  ) dependsOn domain

lazy val domain = (
  ScalaTestProject("domain")
    settings (
    )
  )
