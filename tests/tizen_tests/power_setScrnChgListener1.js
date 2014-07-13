/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
var __result2;
function onScreenStateChanged(previousState, changedState) {
 __result2 = previousState;
}

var __result1 = tizen.power.setScreenStateChangeListener(onScreenStateChanged);
var __expect1 = undefined;
var __expect2 = "SCREEN_OFF"