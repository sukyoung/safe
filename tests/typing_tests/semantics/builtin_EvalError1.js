/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = new EvalError("111");

var __result1 = x.name;
var __expect1 = "EvalError";

var __result2 = x.message;
var __expect2 = "111";

var __result3 = x.toString();
var __expect3 = "EvalError: 111";