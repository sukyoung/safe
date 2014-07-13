/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf

import kr.ac.kaist.jsaf.useful.Files
import kr.ac.kaist.jsaf.useful.Path
import kr.ac.kaist.jsaf.useful.StringMap
import kr.ac.kaist.jsaf.useful.Useful
import _root_.java.io.File
import _root_.java.io.FileNotFoundException
import _root_.java.io.IOException

object ProjectProperties {
  def jsAutoHome() = {
    var s = ""
    s = System.getenv("JS_HOME")
    if (s == null || s.equals("")) {
      try {
        val p = new Path(System.getProperty("java.class.path"))
        val f = p.findDir("../../../src")
        try {
          s = (new File(f, "..")).getCanonicalPath
        } catch {
          case ex:IOException =>
            throw new Error("Failure to evaluate relative path .. from " + f)
        }
      } catch {
        case ex1:FileNotFoundException =>
          throw new Error("Could not find JS_HOME or probing classpath.")
      }
    }
    Files.windowPathToUnixPath(s)
    //s
  }

  val JS_HOME = jsAutoHome

  /**
   * This static field holds the absolute path of the (sub)project location, as
   * computed by reflectively finding the file location of the unnamed
   * package, and grabbing the parent directory.
   * <p/>
   * The path name includes a trailing slash!
   */
  val BASEDIR = searchDef("BASEDIR", "BASEDIR", JS_HOME)

  /**
   * Searches for property/environment definition in the following order
   * <p/>
   * System.getProperty(asProp)
   * System.getenv(asEnv)
   */
  def searchDef(asProp: String, asEnv: String, defaultValue: String) = {
    var result = System.getProperty(asProp)
    if (result == null) result = System.getenv(asEnv)
    if (result == null) result = defaultValue
    result
  }

  val allProps = new StringMap.ComposedMaps(new StringMap.FromReflection(ProjectProperties.getClass,
                                                                         "JS_"),
                                            new StringMap.FromSysProps(),
                                            new StringMap.FromPair("JS_HOME", JS_HOME),
                                            new StringMap.FromEnv())

  /**
   * Get a String property or environment variable.
   * Use property.naming.conventions, these are automatically
   * translated to ENVIRONMENT_NAMING_CONVENTIONS for environment
   * variables and fields of ProjectProperties itself.
   */
  def get(s: String) =
    if (s == null) s
    else Useful.substituteVarsCompletely(s, allProps, 1000)
}
