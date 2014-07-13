/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.tests

import _root_.java.util.{List => JList}
import _root_.java.io.File
import _root_.java.io.FilenameFilter
import _root_.java.io.FileNotFoundException
import _root_.java.io.PrintStream
import _root_.java.util.StringTokenizer
import _root_.kr.ac.kaist.jsaf.useful.ArrayBackedList
import scala.collection.JavaConversions
import junit.framework.Assert._
import junit.framework.Test
import junit.framework.TestCase
import junit.framework.TestResult
import junit.framework.TestSuite
import kr.ac.kaist.jsaf.ProjectProperties
import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.scala_src.useful.Arrays._
import kr.ac.kaist.jsaf.useful.StringMap
import kr.ac.kaist.jsaf.useful.Useful
import kr.ac.kaist.jsaf.useful.WireTappedPrintStream

object FileTests {
  /* Import Note!
   * Do not use Scala's println.  Use System.out.println.
   */

  def makeTestFileName(name: String) =
    if (name.endsWith(".js") || name.endsWith(".widl")) name else name + ".js"

  def join(dir: String, file: String) =
    if (dir.length == 0) file else dir + "/" + file

  def directoryAsFile(dirname: String): File = {
    val dir = new File(dirname)
    if (!dir.exists) {
      System.err.println(dirname + " does not exist")
      throw new FileNotFoundException(dirname)
    }
    if (!dir.isDirectory) {
      System.err.println(dirname + " exists but is not a directory")
      throw new IllegalArgumentException(dirname)
    }
    dir
  }

  def compilerSuite(dirname: String, failsOnly: Boolean,
                    expect_failure: Boolean) = {
    val dir = directoryAsFile(dirname)
    //System.err.println(dir)
    //val shuffled = shuffle(new File(dirname).list())
    suiteFromListOfFiles(/*shuffled*/ new File(dirname).list(),
                         dirname, dirname, dir.getCanonicalPath,
                         failsOnly, expect_failure)
  }

  def suiteFromListOfFiles(shuffledL: JList[String],
                           dir_from_user: String,
                           dir_slashes_normalized: String,
                           dir_canonical: String,
                           failsOnly: Boolean,
                           expect_failure: Boolean): TestSuite = {
    val shuffled = JavaConversions.asScalaBuffer(new ArrayBackedList(shuffledL)).toArray
    suiteFromListOfFiles(shuffled, dir_from_user, dir_slashes_normalized,
                         dir_canonical, failsOnly, expect_failure)
  }

  def suiteFromListOfFiles(shuffled: Array[String],
                           dir_from_user: String,
                           dir_slashes_normalized: String,
                           dir_canonical: String,
                           failsOnly: Boolean,
                           expect_failure: Boolean): TestSuite = {
    var dir_name_from_user = dir_from_user
    var dir_name_slashes_normalized = dir_slashes_normalized
    var dir_name_canonical = dir_canonical
    val testCount = Integer.MAX_VALUE
    var i = testCount
    val suite = new TestSuite("Runs all tests in " + dir_name_from_user) {
                  override def run(result: TestResult) {
                    super.run(result)
                  }
                }
    var commandTests = List[Test]()
    for (one <- shuffled if i > 0) {
      var s = one
      val slashi = s.lastIndexOf('/')
      if (slashi != -1) {
        val candidatedir = s.substring(0, slashi)
        s = s.substring(slashi + 1)
        dir_name_slashes_normalized = candidatedir
        dir_name_canonical = directoryAsFile(dir_name_slashes_normalized).getCanonicalPath
      }
      var decrement = true
      val shouldFail = s.startsWith("XXX")
      if (s.endsWith(".js") || s.endsWith(".widl")) {
        // do nothing
        decrement = false
      } else if (!s.startsWith(".")) {
        if (s.endsWith(".test")) { // need to define the test of tests.
          val propFileName = join(dir_name_canonical, s)
          //var props = new StringMap.FromFileProps(propFileName)
          val props = new StringMap.ComposedMaps(new StringMap.FromFileProps(propFileName),
                                                 new StringMap.FromPair("JS_HOME", ProjectProperties.JS_HOME),
                                                 new StringMap.FromEnv)
          if (props.isEmpty)
            throw new java.io.FileNotFoundException("File not found or empty: "+propFileName)
          val testname = s.substring(0, s.lastIndexOf(".test"))
          var testNames = props.get("tests")
          if (testNames == null) testNames = ""
          else testNames = testNames.trim
          if (testNames.length > 0) {
            val st = new StringTokenizer(testNames)
            while (st.hasMoreTokens)
                commandTests ++= standardCompilerTests(props,
                                                       dir_name_canonical,
                                                       dir_name_slashes_normalized,
                                                       st.nextToken,
                                                       expect_failure,
                                                       shouldFail,
                                                       failsOnly)
          } else commandTests ++= standardCompilerTests(props,
                                                        dir_name_canonical,
                                                        dir_name_slashes_normalized,
                                                        testname,
                                                        expect_failure,
                                                        shouldFail,
                                                        failsOnly)
        } else {
          System.out.println("Not compiling file " + s)
          decrement = false
        }
      }
      if (decrement) i -= 1
    }
    if (i <= 0) System.out.println("Early testing exit after " + testCount + " tests")
    // Do all the larger tests
    else for (test <- commandTests) suite.addTest(test)
    suite
  }

  def standardCompilerTests(props: StringMap,
                            canonicalDirName: String,
                            dirname: String,
                            testname: String,
                            expect_not_passing: Boolean,
                            shouldFail: Boolean,
                            failsOnly: Boolean): List[Test] = {
    var cTests = List[Test]()
    val commands = List("strict","compile","disambiguate","interpret","interpret_mozilla","cfg",
                        "concolic","bug-detector","bug-detector-loc","widlparse","widlcheck")
    var found = false
    for (c <- commands)
      if (props.get(c) != null) {
        cTests ::= (new CommandTest(c,
                                    props,
                                    canonicalDirName,
                                    dirname,
                                    testname,
                                    failsOnly,
                                    expect_not_passing,
                                    shouldFail))
        found = true
      }
    if (!found)
      throw new IllegalArgumentException("No supported tests found in " +
                                         dirname + "/" + testname)
    cTests
  }

  class CommandTest(command: String,
                    props: StringMap,
                    path: String,
                    d: String,
                    s: String,
                    unexpected_only: Boolean,
                    knownFailure: Boolean,
                    shouldFail: Boolean) extends SourceFileTest(path, d, s,
                                                                unexpected_only,
                                                                knownFailure,
                                                                shouldFail) {
    override def justTheTest = {
      val tokens = if (command.equals("widlcheck"))
                     Array[String](command, "-js", join(dir, makeTestFileName(name)),
                                   "-db", "tests/widlchecker_tests/webapis.db")
                   else if (command.equals("bug-detector-loc"))
                     Array[String]("bug-detector", "-nostop", join(dir, makeTestFileName(name)))
                   else
                     Array[String](command, join(dir, makeTestFileName(name)))
      /*
System.out.print("[[ ")
for (s <- tokens) System.out.print(s+ " ")
System.out.println(" ]]... Calling... ")
      */
      kr.ac.kaist.jsaf.Shell.params.Clear()
      kr.ac.kaist.jsaf.Shell.subMain(tokens)
    }

    override def tag = command

    override def testFailed(out: String, err: String, exc: String): String =
      generalTestFailed(command + "_", props, out, err, exc)
  }

  abstract class SourceFileTest(path: String,
                                d: String,
                                s: String,
                                unexpected_only: Boolean,
                                knownFailure: Boolean,
                                shouldFail: Boolean)
           extends BaseTest(path, d, s, unexpected_only,
                            knownFailure, shouldFail) {
    def tag(): String
    def testFile(): Unit = {
      // Useful when a test is running forever
      //   System.out.println(this.name)
      //   System.out.flush()
      val checkDuration: Boolean = false
      if(checkDuration) System.out.println(" " + s + " is being executed...")

      val oldErr = System.err
      val oldOut = System.out
      val wt_err = WireTappedPrintStream.
      make(new PrintStream(System.err, true, "UTF-8"), unexpected_only)
      val wt_out = WireTappedPrintStream.make(new PrintStream(System.out, true, "UTF-8"), unexpected_only)
      System.setErr(wt_err)
      System.setOut(wt_out)

      val start = System.nanoTime
      //val jsFile = f + ".js"
      var rc = 0
      try {
        try {
            /*
          oldOut.print(" " + tag + " ")
          oldOut.print(f)
          oldOut.print("\n")
          oldOut.flush
            */
          //in = Useful.utf8BufferedFileReader(jsFile)
          rc = justTheTest
        } finally {
          System.setErr(oldErr)
          System.setOut(oldOut)
        }
      } catch {
        case ex:Throwable =>
          val outs = wt_out.getString
          val errs = wt_err.getString
          var exFirstLine = ex.toString
          val trueFailure = testFailed(outs, errs, exFirstLine)
          if (f.contains("XXX")) {
            if (trueFailure != null) {
              unexpectedExceptionBoilerplate(wt_err, wt_out, ex,
                                             " Did not satisfy " + trueFailure)
              return
            } else {
              // "Failed", but correctly
              // !unexpectedOnly || expectFailure
              wt_err.flush(printSuccess)
              wt_out.flush(printSuccess)
              var crLoc = exFirstLine.indexOf("\n")
              if (crLoc == -1) crLoc = exFirstLine.length
              exFirstLine = exFirstLine.substring(0, crLoc)
              if (printSuccess) System.out.println(exFirstLine)
              //System.out.println(" OK Saw expected exception")
              return
            }
          } else unexpectedExceptionBoilerplate(wt_err, wt_out, ex,
                                                " UNEXPECTED exception ")
      }
      /* Come here IFF NO EXCEPTIONS, to analyze output */
      val outs = wt_out.getString
      val errs = wt_err.getString
      val anyFails = (outs.contains("fail") || outs.contains("FAIL") ||
                      errs.contains("fail") || errs.contains("FAIL") ||
                      rc != 0) && (!f.contains("string-unpack-code"))
      var trueFailure = testFailed(outs, errs, "")
      if (shouldFail) {
        // NOTE expect to see this on STANDARD OUTPUT, not ERROR.
        if (anyFails && trueFailure == null) {
          wt_err.flush(printSuccess)
          wt_out.flush(printSuccess)
          // Saw a failure, that is good.
          //System.out.println(" Saw expected failure")
        } else {
          if (printFailure) System.out.println
          wt_err.flush(printFailure)
          wt_out.flush(printFailure)
          if (trueFailure != null) {
            System.out.println(" Saw failure, but did not satisfy " + trueFailure)
            // Expected exception, saw none.
            fail("Saw wrong failure.")
          } else {
            System.out.println(" Missing expected failure.")
            // Expected exception, saw none.
            fail("Expected failure or exception, saw none.")
          }
        }
      } else {
        // This logic is a little confusing.
        // Failure is failure.  TrueFailure contains the better message.
        if (anyFails && trueFailure == null)
          trueFailure = "FAIL or fail should not appear in output"
        val duration = (System.nanoTime - start) / 1000000
        if (trueFailure != null) {
          if(checkDuration) System.out.println(" " + s + " FAIL (time = " + duration + "ms)")
          else System.out.println(" FAIL")
          wt_err.flush(printFailure)
          wt_out.flush(printFailure)
        } else {
          if(checkDuration) System.out.println(" " + s + " OK (time = " + duration + "ms)")
          wt_err.flush(printSuccess)
          wt_out.flush(printSuccess)
        }
        assertTrue("Must satisfy " + trueFailure, trueFailure == null)
      }
    }

    def unexpectedExceptionBoilerplate(wt_err: WireTappedPrintStream,
                                       wt_out: WireTappedPrintStream,
                                       ex: Throwable,
                                       s: String) = {
      if (printFailure) System.out.println
      wt_err.flush(printFailure)
      wt_out.flush(printFailure)
      if (printFailure) {
        System.out.println(s)
        ex.printStackTrace
        fail
      } else {
        System.out.println(s)
        fail(ex.getMessage)
      }
    }

    def justTheTest(): Int
  }

  class BaseTest(path: String,
                 _dir: String,
                 _name: String,
                 unexpected_only: Boolean,
                 knownFailure: Boolean,
                 shouldFail: Boolean) extends TestCase("testFile") {
    val dir = _dir
    val name = _name
    // Directory-qualified file name
    val f = join(dir, name)
    val printSuccess = !unexpected_only || knownFailure
    val printFailure = !unexpected_only || !knownFailure
    override def getName = f

    /**
     * Returns true if this test should be regarded as a "Failure",
     * regardless of the XXX test name or not.  This can be used to
     * test that a particular exception was thrown, for example; not only
     * should (say) XXXbadNumber thrown an exception, it should throw
     * a NumberFormatException.  Thus, the exc string could be tested
     * to see that it contains "NumberFormatException".
     */
    def testFailed(out: String, err: String, exc: String) = ""

    /**
     * Looks for properties of the form
     * compile/link/run_
     * out/err/exceptions_
     * contains/matches/WImatches/equals/WCIequals,
     * returns true if an expected condition fails.
     */
    def generalTestFailed(pfx: String, props: StringMap, out: String, err: String,
                          exc: String): String = {
      var s = ""
      if (s == "") s = generalTestFailed(pfx, props, "out", out)
      if (s == "") s = generalTestFailed(pfx, props, "err", err)
      if (s == "") s = generalTestFailed(pfx, props, "exception", exc)
      s
    }

    def generalTestFailed(pfx: String, props: StringMap, which: String,
                          contents: String): String = {
      var any_check = false
      var what = pfx + which + "_contains"
      var test = props.get(what)
      test = ProjectProperties.get(test)
      if (test != null && test.length > 0) {
        if (!contents.contains(test)) return what + "; expected\n" + test
        any_check = true
      }

      what = pfx + which + "_does_not_contain"
      test = props.get(what)
      test = ProjectProperties.get(test)
      if (test != null && test.length > 0) {
        if (contents.contains(test)) return what + "; expected\n" + test
        any_check = true
      }

      what = pfx + which + "_matches"
      test = props.get(what)
      test = ProjectProperties.get(test)
      if (test != null && test.length > 0) {
        if (!contents.matches(test)) return what + "; expected\n" + test
        any_check = true
      }

      what = pfx + which + "_WImatches"
      test = props.get(what)
      test = ProjectProperties.get(test)
      if (test != null && test.length() > 0) {
        val wi_contents = contents.replaceAll("\\s+", " ").trim
        if (!wi_contents.matches(test)) return what + "; expected\n" + test
        any_check = true
      }

      what = pfx + which + "_WCIequals"
      test = props.get(what)
      test = props.getCompletely(test)
      if (test != null && test.length > 0) {
        val wci_contents = contents.replaceAll("\\s+", " ").trim
        val wci_test = test.replaceAll("\\s+", " ").trim
        if (!wci_contents.equals(wci_test))
          return what + "; expected\n" + test
        any_check = true
      }

      what = pfx + which + "_equals"
      test = props.get(what)
      test = props.getCompletely(test)
      if (test != null && test.length > 0) {
        var wi_contents = contents.replaceAll("[ \\\t]+", " ")
        var wi_test = test.replaceAll("[ \\\t]+", " ")
        // Convert Windows CRLF to UNIX LF
        wi_contents = wi_contents.replaceAll("\\\r\\\n", "\n")
        wi_test = wi_test.replaceAll("\\\r\\\n", "\n")
        // Convert Mac CR to UNIX LF
        wi_contents = wi_contents.replaceAll("\\\r", "\n")
        wi_test = wi_test.replaceAll("\\\r", "\n")

        if (!wi_contents.equals(wi_test)) {
          if (wi_contents.trim.equals(wi_test.trim)) {
            // It is a leading/trailing whitespace problem....
            val c0 = wi_contents.charAt(0)
            val t0 = wi_test.charAt(0)
            val cN = wi_contents.charAt(wi_contents.length - 1)
            val tN = wi_test.charAt(wi_test.length - 1)
            var problem = ""
            if (c0 == ' ' || c0 == '\n') {
              if (t0 == ' ' || t0 == '\n') {
              } else problem += "text began with unexpected whitespace"
            } else {
              if (t0 == ' ' || t0 == '\n')
                problem += "text began without expected whitespace"
              else {}
            }
            val problemAnd = if (problem.length > 0) problem + " and " else problem
            if (cN == ' ' || cN == '\n') {
              if (tN == ' ' || tN == '\n') {
              } else problem = problemAnd + "text ended with unexpected whitespace"
            } else {
              if (tN == ' ' || tN == '\n')
                problem = problemAnd + "text ended without expected whitespace"
              else {}
            }
            return what + ": " + problem
          } else if (wi_contents.replaceAll("\\s+", " ").trim.equals(wi_test.replaceAll("\\s+",
                                                                                        " ").trim)) {
            val cL = wi_contents.replaceAll("\n[ \t]+", "\n").trim
            val cT = wi_contents.replaceAll("[ \t]+\n", "\n").trim
            val tL = wi_test.replaceAll("\n[ \t]+", "\n").trim
            val tT = wi_test.replaceAll("[ \t]+\n", "\n").trim
            if (cL.equals(tL))
              return what + ": different LEADING whitespace on some line(s)"
            else if (cT.equals(tT))
              return what + ": different TRAILING whitespace on some line(s)"
            else if (wi_contents.replaceAll("[ \\\t]*\\\n[ \\\t]*", "\n").trim.equals(wi_test.replaceAll(
                                "[ \\\t]*\\\n[ \\\t]*",
                                "\n").trim))
              return what + ": different LEADING AND TRAILING whitespace on some line(s)"
            return what + ": some sort of an internal whitespace problem (linebreaks?)"
          }
          return what + "; expected\n" + test
        }
        any_check = true
      }
      if (!any_check && pfx.equals("run_") && which.equals("out")) {
        // If there is no check specified on run_out, demand that it
        // contain "pass" or "PASS".
        if (!(contents.contains("pass") || contents.contains("PASS")))
          return "default check run_out_contains=PASS"
      }
      return null
    }
  }
}
