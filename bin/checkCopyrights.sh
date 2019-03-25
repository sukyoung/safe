#!/bin/sh

################################################################################
#    Copyright (c) 2016-2018, KAIST.
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
  files_2018_java=`find $dir -name "*.java" -newer ../timestamp -print`
  files_2018_rats=`find $dir -name "*.rats" -newer ../timestamp -print`
  files_2018_scala=`find $dir -name "*.scala" -newer ../timestamp -print`
  files_2018_scala=`find $dir -name "*.sh" -newer ../timestamp -print`

  for fil in $files_2018_java $files_2018_rats $files_2018_scala
  do
    res=`grep "2018, KAIST" $fil`
    if strdiff "$res" "2018, KAIST" ; then
      if (strdiff "$fil" "main/java/kr/ac/kaist/safe/parser/JS.java") ; then
        echo "  $fil"
      fi
    fi
  done
  cd ..
done
