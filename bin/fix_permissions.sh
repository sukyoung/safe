#!/bin/bash

################################################################################
#    Copyright (c) 2012-2013, KAIST.
#    All rights reserved.
#
#    Use is subject to license terms.
#
#    This distribution may include materials developed by third parties.
################################################################################

export JS_HOME="`$(dirname $0)/js_home`"

chmod +x $JS_HOME/bin/cluster.sh
chmod +x $JS_HOME/bin/jsaf
chmod +x $JS_HOME/bin/jscd_configure
chmod +x $JS_HOME/bin/jscd_out2xml.sh
chmod +x $JS_HOME/bin/jscd_vertical-param-batch
chmod +x $JS_HOME/lib/deckard/scripts/clonedetect/*
chmod +x $JS_HOME/lib/deckard/src/lsh/bin/*
chmod +x $JS_HOME/lib/deckard/src/main/out2xml
chmod +x $JS_HOME/lib/deckard/src/vgen/vgrouping/computeranges
chmod +x $JS_HOME/lib/deckard/src/vgen/vgrouping/dispatchvectors
chmod +x $JS_HOME/lib/deckard/src/vgen/vgrouping/rundispatch
chmod +x $JS_HOME/lib/deckard/src/vgen/vgrouping/rundispatchonefile
chmod +x $JS_HOME/lib/deckard/src/vgen/vgrouping/runsplit
chmod +x $JS_HOME/lib/deckard/src/vgen/vgrouping/runvectorsort

