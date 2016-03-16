#!/bin/bash

################################################################################
#    Copyright (c) 2016, KAIST.
#    All rights reserved.
#
#    Use is subject to license terms.
#
#    This distribution may include materials developed by third parties.
################################################################################

export WKSPACE=$SAFE_HOME/tests
export JSSPACE=$WKSPACE/js
export RSSPACE=$WKSPACE/result

cd $RSSPACE
rm -f success/astRewrite/*.test
rm -f success/compile/*.test
rm -f success/cfg/*.test
rm -f error/astRewrite/*.test

cd $WKSPACE
succ_files=`find js/success -name "*.js" -print`

for fil in $succ_files
do
  prename=`basename $fil`
  name=${prename%.js}
  ast_out=$RSSPACE/success/astRewrite/$name.test
  com_out=$RSSPACE/success/compile/$name.test
  cfg_out=$RSSPACE/success/cfg/$name.test

  echo "create $ast_out"
  `safe astRewrite $fil > $ast_out`
  echo "create $com_out"
  `safe compile $fil > $com_out`
  echo "create $cfg_out"
  `safe cfg $fil > $cfg_out`
done
