#!/bin/sh

################################################################################
#    Copyright (c) 2016, KAIST.
#    All rights reserved.
#
#    Use is subject to license terms.
#
#    This distribution may include materials developed by third parties.
################################################################################

export WKSPACE=$SAFE_HOME

cd $WKSPACE

# returns OK if $1 does NOT contain $2
strdiff() {
  [ "${1#*$2*}" = "$1" ] && return 0
  return 1
}

for dir in $WKSPACE/src $WKSPACE/tests
do
  cd $dir
  files_2016_java=`find $dir -name "*.java" -newer ../timestamp -print`
  files_2016_rats=`find $dir -name "*.rats" -newer ../timestamp -print`
  files_2016_scala=`find $dir -name "*.scala" -newer ../timestamp -print`

  for fil in $files_2016_java $files_2016_rats $files_2016_scala
  do
    res=`grep "2016, KAIST" $fil`
    if strdiff "$res" "2016, KAIST" ; then
      if (strdiff "$fil" "main/java/kr/ac/kaist/safe/parser/JS.java") ; then
        echo "  $fil"
      fi
    fi
  done
  cd ..
done
