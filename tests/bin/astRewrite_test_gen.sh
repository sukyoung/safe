#!/bin/bash

################################################################################
#    Copyright (c) 2016-2018, KAIST.
#    All rights reserved.
#
#    Use is subject to license terms.
#
#    This distribution may include materials developed by third parties.
################################################################################

export WKSPACE=$SAFE_HOME/tests/cfg
export JSSPACE=$WKSPACE/js
export RSSPACE=$WKSPACE/result

cd $RSSPACE
rm -f success/astRewrite/*.test

cd $WKSPACE
succ_files=`find js/success -name "*.js" -print`

echo "generating parse tests..."
for fil in $succ_files
do
  prename=`basename $fil`
  name=${prename%.js}
  ast_out=$RSSPACE/success/astRewrite/$name.test

  echo "create $ast_out"
  $SAFE_HOME/bin/safe astRewrite -silent -astRewriter:out=$ast_out $fil
done
