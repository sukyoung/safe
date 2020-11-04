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
rm -f success/compile/*.test

cd $WKSPACE
succ_files=`find js/success -name "*.js" -print`

echo "generating compile tests..."
for fil in $succ_files
do
  prename=`basename $fil`
  name=${prename%.js}
  com_out=$RSSPACE/success/compile/$name.test

  echo "create $com_out"
  $SAFE_HOME/bin/safe translate -silent -translator:out=$com_out $fil
done
