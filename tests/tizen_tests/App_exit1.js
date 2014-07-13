/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var app = tizen.application.getCurrentApplication();

var __result3 = app.contextId;
var __expect3 = "a";

var __result1 = app.exit();
var __expect1 = undefined;
var __result2 = app.contextId;
var __expect2 = undefined;
