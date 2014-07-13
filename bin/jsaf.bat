@ECHO OFF
REM ################################################################################
REM #    Copyright (c) 2012-2014, KAIST.
REM #    All rights reserved.
REM #
REM #    Use is subject to license terms.
REM #
REM #    This distribution may include materials developed by third parties.
REM ################################################################################
set SV=2.9.2
set TP="%JS_HOME%/lib"
set BUILD="%JS_HOME%/target/scala-%SV%/classes"
ECHO ON

java -Xms128m -Xmx512m -cp "%BUILD%;%TP%/junit.jar;%TP%/commons-lang3-3.1.jar;%TP%/wala.util.jar;%TP%/wala.cast.jar;%TP%/wala.cast.js.jar;%TP%/jericho-html-3.3.jar;%JS_HOME%/bin/xtc.jar;%TP%/plt.jar;%TP%/astgen.jar;%TP%/scala-compiler-%SV%.jar;%TP%/scala-library-%SV%.jar;%TP%/lift-json_2.9.1-2.4.jar;%TP%/nekohtml.jar;%TP%/xercesImpl.jar;%TP%/xml-apis.jar;%TP%/z3/com.microsoft.z3.jar;%TP%/jline-2.12.jar" kr.ac.kaist.jsaf.Shell %1 %2 %3 %4 %5 %6 %7 %8 %9
