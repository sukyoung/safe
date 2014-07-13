/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result2;
try {
var sharedDir = tizen.application.getAppSharedURI("org.tizen.calculator");

var __result1 = sharedDir;
var __expect1 = "";
}
catch (e) {
   __result2 = e.name;
}
var __expect2 = "NotFoundError";