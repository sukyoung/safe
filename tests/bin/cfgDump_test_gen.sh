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

cd $RSSPACE
find . -type f -name '*.json' -delete

cd $WKSPACE
echo "generating cfgDump tests..."

succ_files=`find language -name "*.js" -print`
for fil in $succ_files
do
  prename=`basename $fil`
  name=${prename%.js}
  cfg_dump=$RSSPACE/language/$name

  echo "create $cfg_dump.json"
  $SAFE_HOME/bin/safe cfgBuild -silent -cfgBuilder:json=$cfg_dump $fil
done

succ_files=`find builtin -name "*.js" -print`
for fil in $succ_files
do
  prename=`basename $fil`
  name=${prename%.js}
  cfg_dump=$RSSPACE/builtin/$name

  echo "create $cfg_dump.json"
  $SAFE_HOME/bin/safe cfgBuild -silent -cfgBuilder:json=$cfg_dump $fil
done
