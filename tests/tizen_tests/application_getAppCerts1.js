/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var a = tizen.application.getAppCerts();

var __result1 = a[0].type;
var __expect1 = "AUTHOR_ROOT"
var __result2 = a[0].value;
var __expect2 = "abcd"