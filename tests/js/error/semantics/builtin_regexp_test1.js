/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var o = new RegExp("^(?:\\s*(<[\\w\\W]+>)[^>]*|#([\\w-]*))$");

var r = o.test("#abc");
var __result1 = r;
var __expect1 = true;

