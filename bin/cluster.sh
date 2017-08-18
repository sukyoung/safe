#!/bin/bash

################################################################################
#    Copyright (c) 2017, KAIST.
#    All rights reserved.
#
#    Use is subject to license terms.
#
#    This distribution may include materials developed by third parties.
################################################################################

if (uname | egrep CYGWIN > /dev/null) ; then
  SEP=";"
else
  SEP=":"
fi
 
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
$SAFE_HOME/bin/jscd_vertical-param-batch overwrite

echo "To transform the clone reports into xml, type the following command:"
echo
echo "    $SAFE_HOME/bin/jscd_out2xml.sh"
echo
