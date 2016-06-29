import scalariform.formatter.preferences._
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

parseTest := (testOnly in Test).toTask(s" -- -n ParseTest").value
astRewriteTest := (testOnly in Test).toTask(s" -- -n ASTRewriteTest").value
compileTest := (testOnly in Test).toTask(s" -- -n CompileTest").value
cfgBuildTest := (testOnly in Test).toTask(s" -- -n CFGBuildTest").value
analyzeTest := (testOnly in Test).toTask(s" -- -n AnalyzeTest").value

lazy val root = (project in file(".")).
  settings(
    name := "SAFE",
    version := "2.0",
    organization := "kr.ac.kaist.safe",
    scalaVersion := "2.11.7",
    checkCopyrights in Compile := {
      val violated: String = (baseDirectory.value + "/bin/checkCopyrights.sh" !!)
      if (violated != "") {
        throw new Error("\nFix the copyright(s) of the following:\n" + violated)
      }
    },
    buildParsers in Compile := {
      val xtcFile = new File("./lib/xtc.jar")
      if (!xtcFile.exists) IO.download(new URL("http://cs.nyu.edu/rgrimm/xtc/xtc.jar"), xtcFile)
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
    test <<= (test in Test) dependsOn compile
  )

scalacOptions in ThisBuild ++= Seq("-deprecation", "-feature",
                                   "-language:postfixOps",
                                   "-language:implicitConversions")

unmanagedJars in Compile ++= Seq(file("lib/xtc.jar"), file("jline-2.12.jar"))
cleanFiles ++= Seq(file("src/main/java/kr/ac/kaist/safe/parser/"))

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test" withSources,
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.1" withSources
)
