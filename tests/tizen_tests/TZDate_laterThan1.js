/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var a = new tizen.TZDate(2013, 6, 27, 16, 32, 22, 10, "GMT");
var b = new tizen.TZDate(2013, 6, 25, 10);
var __result1 = a.laterThan(b);
var __expect1 = true;