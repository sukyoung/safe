#!/bin/bash

################################################################################
#    Copyright (c) 2012, KAIST.
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
 
if [ -z "$JAVA_HOME" ] ; then
  JAVACMD=java
else
  JAVACMD="$JAVA_HOME/bin/java"
fi

if [ -z "$JAVA_FLAGS" ] ; then
  JAVA_FLAGS="-Xmx256m  -Xss512m"
fi

. $JS_HOME/bin/jscd_configure
errcode=$?
if [[ $errcode -ne 0 ]]; then
	exit $errcode
fi

find "$CLUSTER_DIR" -type f ! -name "*.xml" -name "post_cluster_vdb_[0-9]*_[0-9]*_allg_[0-1].[0-9]*_[0-9]?" | while read cdb;
do
	basecdb=`basename "$cdb"`
	basecdb=${basecdb#post_}
	echo -n "Transforming ${CLUSTER_DIR}/post_$basecdb..."
	perl -i -pe 'if (defined $x && /\S/) { print $x; $x = ""; } $x .= "\n" x chomp; s/\s*?$//; 1 while s/^(\t*) /$1\t/; if (eof) {$x = ""; }' "${CLUSTER_DIR}/post_$basecdb"
	"$DECKARD_DIR/src/main/out2xml" "${CLUSTER_DIR}/post_$basecdb" > "${CLUSTER_DIR}/post_${basecdb}.xml"
	perl -pi -le 'print "<?xml-stylesheet type=\"text/xsl\" href=\"../../doc/jscd_html.xslt\"?>" if $. == 2' "${CLUSTER_DIR}/post_${basecdb}.xml"
	perl -pi -e 's/&/&amp;/g' "${CLUSTER_DIR}/post_${basecdb}.xml"
	perl -pi -e 's/\?/%3F/g if $. > 2' "${CLUSTER_DIR}/post_${basecdb}.xml"
	echo Done
	echo "See xml in ${CLUSTER_DIR}/post_${basecdb}.xml"
	echo
done
