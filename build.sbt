import scalariform.formatter.preferences._
import scala.sys.process._

lazy val checkCopyrights = taskKey[Unit]("Checks copyrights of source files")
lazy val buildParsers = taskKey[Unit]("Builds parsers")
lazy val deleteParserDir = taskKey[Unit]("Delete java parser directory")

// short-cuts
lazy val basicAnalyzeTest = taskKey[Unit]("Launch basic analyze tests")
lazy val benchTest = taskKey[Unit]("Launch benchmarks tests")
lazy val cfgBuildTest = taskKey[Unit]("Launch cfg build tests")
lazy val htmlTest = taskKey[Unit]("Launch html tests")
lazy val test262Test = taskKey[Unit]("Launch test262 tests")

lazy val root = (project in file(".")).
  settings(
    name := "SAFE",
    version := "2.0",
    organization := "kr.ac.kaist.safe",
    scalaVersion := "2.12.6",
    Compile / checkCopyrights := {
      val violated: String = (baseDirectory.value + "/bin/checkCopyrights.sh" !!)
      if (violated != "") {
        throw new Error("\nFix the copyright(s) of the following:\n" + violated)
      }
    },
    Compile / buildParsers := {
      // webix
      url("http://cdn.webix.com/edge/webix.js") #> file("./src/main/resources/assets/js/webix.js")
      url("http://cdn.webix.com/edge/webix.css") #> file("./src/main/resources/assets/js/webix.css")

      // xtc
      val xtcFile = file("./lib/xtc.jar")
      if (!xtcFile.exists) {
        // TODO exception handling: not downloaded
        url("http://central.maven.org/maven2/xtc/rats/2.4.0/rats-2.4.0.jar") #> xtcFile
      }

      val options = ForkOptions().withBootJars(Vector(xtcFile))
      val srcDir = baseDirectory.value + "/src/main"
      val inDir = srcDir + "/scala/kr/ac/kaist/safe/parser/"
      val outDir = srcDir + "/java/kr/ac/kaist/safe/parser/"
      val outFile = file(outDir)
      if (!outFile.exists) IO.createDirectory(outFile)
      val arguments = Seq("-in", srcDir + "/scala", "-enc-out", "UTF-8",
                          "-out", outDir, inDir + "JS.rats")
      val mainClass = "xtc.parser.Rats"
      val cache = FileFunction.cached(outFile,
                                      FilesInfo.lastModified,
                                      FilesInfo.exists) {
        in: Set[File] => {
          Fork.java(options, mainClass +: arguments)
          Set(file(inDir + "JS.rats"))
        }
      }
      cache(file(inDir).asFile.listFiles.toSet)
    },
    Test / testOptions += Tests.Argument("-fDG", baseDirectory.value + "/tests/detail"),
    Compile / compile := {
      (Compile / compile) dependsOn (Compile / buildParsers, Compile / checkCopyrights)
    }.value,
    test := (Test / testOnly).toTask(
      " kr.ac.kaist.safe.CFGBuildTest" +
      " kr.ac.kaist.safe.BasicAnalyzeTest" +
      " kr.ac.kaist.safe.HTMLAnalyzeTest").value,
    basicAnalyzeTest := (Test / testOnly).toTask(" kr.ac.kaist.safe.BasicAnalyzeTest").value,
    benchTest := (Test / testOnly).toTask(" kr.ac.kaist.safe.BenchAnalyzeTest").value,
    cfgBuildTest := (Test / testOnly).toTask(" kr.ac.kaist.safe.CFGBuildTest").value,
    htmlTest := (Test / testOnly).toTask(" kr.ac.kaist.safe.HTMLAnalyzeTest").value,
    test262Test := (Test / testOnly).toTask(" kr.ac.kaist.safe.Test262AnalyzeTest").value
  )

ThisBuild / scalacOptions ++= Seq("-deprecation", "-feature",
                                   "-language:postfixOps",
                                   "-language:implicitConversions")

Compile / unmanagedJars ++= Seq(file("lib/xtc.jar"), file("lib/jericho-html-3.3.jar"))
cleanFiles ++= Seq(file("src/main/java/kr/ac/kaist/safe/parser/"))

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-library" % scalaVersion.value,
  "org.scala-lang" % "scala-compiler" % scalaVersion.value % "scala-tool",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test" withSources,
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "io.spray" %% "spray-json" % "1.3.2",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.2",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.12" % "2.9.1",
  "org.jline" % "jline" % "3.10.0"
)

javacOptions ++= Seq("-encoding", "UTF-8")

retrieveManaged := true

scalariformPreferences := scalariformPreferences.value
  .setPreference(DanglingCloseParenthesis, Force)
  .setPreference(DoubleIndentConstructorArguments, false)
