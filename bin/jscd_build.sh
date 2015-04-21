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
export CXXFLAGS="-O3"
export CFLAGS="-O3"

(
cd $JS_HOME/lib/deckard/src/vgen/vgrouping/ || exit 1
make clean
make
errcode=$?
if [ $errcode -ne 0 ]; then
	echo "Error: vgrouping make failed. Exit."
	exit $errcode
fi
)

(
cd $JS_HOME/lib/deckard/src/lsh/ || exit 1
make clean_all
make
errcode=$?
if [ $errcode -ne 0 ]; then
	echo "Error: lsh make failed. Exit."
	exit $errcode
fi
)

(
cd $JS_HOME/lib/deckard/src/ptgen/gcc || exit 1
make clean
make
errcode=$?
if [ $errcode -ne 0 ]; then
	echo "Error: gccptgen make failed. Exit."
	exit $errcode
fi
)

(
cd $JS_HOME/lib/deckard/src/vgen/treeTra || exit 1
make clean
make
errcode=$?
if [ $errcode -ne 0 ]; then
	echo "Error: libvgen make failed. Exit."
	exit $errcode
fi
)

(
cd $JS_HOME/lib/deckard/src/main/ || exit 1
make clean
make out2xml
errcode=$?
if [ $errcode -ne 0 ]; then
	echo "Error: out2xml make failed. Exit."
	exit $errcode
fi
)
