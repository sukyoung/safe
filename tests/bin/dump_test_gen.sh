#!/bin/bash

################################################################################
#    Copyright (c) 2017, KAIST.
#    All rights reserved.
#
#    Use is subject to license terms.
#
#    This distribution may include materials developed by third parties.
################################################################################

export WKSPACE=$SAFE_HOME/tests/semantics
export RSSPACE=$WKSPACE/result

cd $WKSPACE
echo "generating interation numbers..."

function f(){
  prename=`basename $1`
  name=${prename%.js}
  json_out=$RSSPACE/$name

  echo "create $json_out.json"

  iter_str=`$SAFE_HOME/bin/safe analyze $1 | grep "iter"`
  iter=`expr ${iter_str:18} / 2`
  printf "jump $iter\ndump $json_out\nrun" | $SAFE_HOME/bin/safe analyze -silent -testMode -analyzer:console $1
}

succ_files=`find language -name "*.js" -print`
for fil in $succ_files
do
  f $fil
done

succ_files=`find builtin -name "*.js" -print`
for fil in $succ_files
do
  f $fil
done
