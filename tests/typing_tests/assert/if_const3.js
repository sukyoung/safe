/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var x = null;
var y = true;
if(Math.random() > 1) x = undefined;
if(Math.random() > 1) x = 1;
if(Math.random() > 1) x = "asdf";
if(x !== undefined) y = x;

var __result1 = x;
var __expect1 = null;

var __result2 = y;
var __expect2 = 1;

var __result3 = y;
var __expect3 = "asdf";
