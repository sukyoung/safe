/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.util

import java.nio.file.Paths
import kr.ac.kaist.safe.BASE_DIR

object NodeJSUtil {
  // TODO : Not fully modeled
  val requireFunction =
    "function __require(path) {\n" +
      "  var resolvedPath = @resolvePath(path);\n" +
      "  if(@ModuleCache[resolvedPath]) {\n" +
      "    return @ModuleCache[resolvedPath].exports;\n" +
      "  }\n" +
      "  else {\n" +
      "    return @loadModule(this, resolvedPath);\n" +
      "  }\n" +
      "}\n"
  val moduleWrapperTemplate =
    "function (){\n" +
      "   var __filename = /* SAFE: __filename */;\n" +
      "   var __dirname = /* SAFE: __dirname */;\n" +
      "   var module = { exports : {}\n" +
      "                };\n" +
      "   @ModuleCache[__filename] = module;\n" +
      "   var require = " +
      requireFunction +
      "   require.cache = @ModuleCache;\n" +
      "   var exports = module.exports;\n" +
      "\n" +
      "   /* SAFE: original source */\n " +
      "\n" +
      "   return module.exports;\n" +
      "}"

  // Translate the original source to a source with a module wrapper
  def moduleWrapper(source: String, filename: String, dirname: String, isMain: Boolean): String = {
    val translatedSource = moduleWrapperTemplate.replace(
      "/* SAFE: __filename */", "\"" + filename + "\""
    ).replace(
        "/* SAFE: __dirname */", "\"" + dirname + "\""
      ).replace(
          "/* SAFE: original source */", source
        )
    translatedSource
  }

  // Translate the original source to a source with the call of the module wrapper
  def moduleWrapperCall(source: String, filename: String, dirname: String, isMain: Boolean): String = {
    val translatedSource = moduleWrapper(source, filename, dirname, isMain)
    "(" + translatedSource + ") ();"
  }
  // NodeJS V1.0
  private val coreModuleList = List(
    "assert", "buffer", "child_process", "cluster", "console",
    "constants", "crypto", "dgram", "dns", "domain",
    "events", "freelist", "fs", "http", "https",
    "module", "net", "os", "path", "process",
    "punycode", "querystring", "readline", "repl", "smalloc",
    "stream", "string_decoder", "sys", "timers", "tls", "tty",
    "url", "util", "v8", "vm", "zlib",
    // internal core modules
    "_debug_agent", "_debugger", "_http_agent", "_http_client", "_http_common",
    "_http_incoming", "_http_outgoing", "_http_server", "_linklist", "_stream_duplex",
    "_stream_passthrough", "_stream_readable", "_stream_transform", "_stream_writable",
    "_tls_common", "_tls_legacy", "_tls_wrap"
  )

  private val coreModuleBase = BASE_DIR + "/src/main/resources/nodejsModels/core_modules/"

  // Resolve the module path
  // Alrorithm : https://nodejs.org/dist/latest-v8.x/docs/api/modules.html#modules_all_together
  // TODO : Not fully implemented - for now, it distinguishes only core modules from others
  def resolve(path: String): String = {
    if (coreModuleList.contains(path))
      coreModuleBase + path + ".js"
    else {
      if (path.startsWith("./") && path.endsWith(".js"))
        Paths.get(path.stripPrefix("./")).toAbsolutePath.toString
      else
        throw new Error("Not Yet Implemented : require.resolve")
    }
  }

  private val modelBase = BASE_DIR + "/src/main/resources/nodejsModels/"

  def getAllModelBase(): List[String] = {
    List(modelBase + "globals/")
  }

}
