#!/bin/bash

################################################################################
#    Copyright (c) 2012-2013, KAIST.
#    All rights reserved.
#
#    Use is subject to license terms.
#
#    This distribution may include materials developed by third parties.
################################################################################

export JS_HOME="`$(dirname $0)/js_home`"

if (uname | egrep CYGWIN > /dev/null) ; then
  SEP=";"
else
  SEP=":"
fi
TP="${JS_HOME}/lib"
SV=2.9.1

CP="${JS_HOME}/build${SEP}${TP}/junit/junit.jar${SEP}${TP}/commons-io/commons-io-2.3.jar${SEP}${TP}/commons-lang/commons-lang3-3.1.jar${SEP}${TP}/wala/wala.util.jar${SEP}${TP}/wala/wala.cast.jar${SEP}${TP}/wala/wala.cast.js.jar${SEP}${TP}/jericho/jericho-html-3.3.jar${SEP}${TP}/wala/wala.classloader.jar${SEP}${TP}/xtc/xtc.jar${SEP}${TP}/plt/plt.jar${SEP}$JAVA_HOME/lib/tools.jar${SEP}${TP}/astgen/astgen.jar${SEP}${TP}/scala/scala-compiler-${SV}.jar${SEP}${TP}/scala/scala-library-${SV}.jar${SEP}${TP}/tajs/htmlparser.jar${SEP}${TP}/tajs/options.jar${SEP}${TP}/tajs/util.jar${SEP}${TP}/tajs/lib/compiler.jar${SEP}${TP}/tajs/lib/jdom-1.1.1.jar${SEP}${TP}/tajs/lib/jdom-contrib-1.1.1.jar${SEP}${TP}/tajs/lib/jtidy.jar${SEP}${TP}/tajs/lib/log4j.jar${SEP}${TP}/xerces/xercesImpl.jar${SEP}${TP}/nekohtml/nekohtml.jar${SEP}${TP}/xerces2/xercesImpl.jar${SEP}${TP}/xerces2/xml-apis.jar${SEP}$CLASSPATH"
 
if [ -z "$JAVA_HOME" ] ; then
  JAVACMD=java
else
  JAVACMD="$JAVA_HOME/bin/java"
fi

if [ -z "$JAVA_FLAGS" ] ; then
  JAVA_FLAGS="-Xmx256m  -Xss512m"
fi

echo "Vector clustering and filtering..."
echo
$JS_HOME/bin/jscd_vertical-param-batch overwrite

echo "To transform the clone reports into xml, type the following command:"
echo
echo "    $JS_HOME/bin/jscd_out2xml.sh"
echo
