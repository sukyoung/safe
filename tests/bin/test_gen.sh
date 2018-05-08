#!/bin/bash

################################################################################
#    Copyright (c) 2016-2018, KAIST.
#    All rights reserved.
#
#    Use is subject to license terms.
#
#    This distribution may include materials developed by third parties.
################################################################################

export WKSPACE=$SAFE_HOME/tests/bin
cd $WKSPACE

./parse_test_gen.sh
./compile_test_gen.sh
./cfgBuild_test_gen.sh
./dump_test_gen.sh
