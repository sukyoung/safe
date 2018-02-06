import java.io.File

lazy val checkCopyrights = taskKey[Unit]("Checks copyrights of source files")
lazy val buildParsers = taskKey[Unit]("Builds parsers")
lazy val deleteParserDir = taskKey[Unit]("Delete java parser directory")

// phase test
lazy val parseTest = taskKey[Unit]("Launch parse tests")
lazy val astRewriteTest = taskKey[Unit]("Launch AST Rewrite tests")
lazy val compileTest = taskKey[Unit]("Launch compile tests")
lazy val cfgBuildTest = taskKey[Unit]("Launch cfg Build tests")
lazy val analyzeTest = taskKey[Unit]("Launch analyze tests")
lazy val htmlTest = taskKey[Unit]("Launch html tests")
lazy val test262Test = taskKey[Unit]("Launch test262 tests")
lazy val benchTest = taskKey[Unit]("Launch benchmarks tests")
// TODO lazy val dumpTest = taskKey[Unit]("Launch dump tests")

lazy val root = (project in file(".")).
  settings(
    name := "SAFE",
    version := "2.0",
    organization := "kr.ac.kaist.safe",
    scalaVersion := "2.12.3",
    checkCopyrights in Compile := {
      val violated: String = (baseDirectory.value + "/bin/checkCopyrights.sh" !!)
      if (violated != "") {
        throw new Error("\nFix the copyright(s) of the following:\n" + violated)
      }
    },
    buildParsers in Compile := {
      // xtc
      val xtcFile = new File("./lib/xtc.jar")
      if (!xtcFile.exists) {
        // TODO exception handling: not downloaded
        IO.download(new URL("https://cs.nyu.edu/rgrimm/xtc/xtc.jar"), xtcFile)
      }

      // webix
      val webixJsFile = new File("./src/main/resources/assets/js/webix.js")
      val webixCssFile = new File("./src/main/resources/assets/css/webix.css")
      if (!webixJsFile.exists)
        IO.download(new URL("http://cdn.webix.com/edge/webix.js"), webixJsFile)
      if (!webixCssFile.exists)
        IO.download(new URL("http://cdn.webix.com/edge/webix.css"), webixCssFile)

      val options = ForkOptions(bootJars = Seq(xtcFile))
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
    testOptions in Test += Tests.Argument("-fDG", baseDirectory.value + "/tests/detail"),
    compile <<= (compile in Compile) dependsOn (buildParsers in Compile, checkCopyrights in Compile),
    test <<= (testOnly in Test).toTask(s" -- -n ParseTest -n ASTRewriteTest -n CompileTest -n CFGBuildTest -n AnalyzeTest -n HtmlTest") dependsOn compile,
    parseTest <<= (testOnly in Test).toTask(s" -- -n ParseTest") dependsOn compile,
    astRewriteTest <<= (testOnly in Test).toTask(s" -- -n ASTRewriteTest") dependsOn compile,
    compileTest <<= (testOnly in Test).toTask(s" -- -n CompileTest") dependsOn compile,
    cfgBuildTest <<= (testOnly in Test).toTask(s" -- -n CFGBuildTest") dependsOn compile,
    analyzeTest <<= (testOnly in Test).toTask(s" -- -n AnalyzeTest") dependsOn compile,
    htmlTest <<= (testOnly in Test).toTask(s" -- -n HtmlTest") dependsOn compile,
    test262Test <<= (testOnly in Test).toTask(s" -- -n Test262Test") dependsOn compile,
    benchTest <<= (testOnly in Test).toTask(s" -- -n BenchTest") dependsOn compile
    // TODO dumpTest <<= (testOnly in Test).toTask(s" -- -n DumpTest") dependsOn compile
  )

scalacOptions in ThisBuild ++= Seq("-deprecation", "-feature",
                                   "-language:postfixOps",
                                   "-language:implicitConversions")

unmanagedJars in Compile ++= Seq(file("lib/xtc.jar"), file("lib/jline-2.12.jar"), file("lib/jericho-html-3.3.jar"))
cleanFiles ++= Seq(file("src/main/java/kr/ac/kaist/safe/parser/"))

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-library" % scalaVersion.value,
  "org.scala-lang" % "scala-compiler" % scalaVersion.value % "scala-tool",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test" withSources,
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "io.spray" %% "spray-json" % "1.3.2",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.2",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.12" % "2.9.1"
)

javacOptions ++= Seq("-encoding", "UTF-8")

retrieveManaged := true
