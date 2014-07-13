/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var obj;
function foo(x) { obj = x; }

var __result1;
var __expect1 = 123;

var __result2;
var __expect2 = "ABC";

if (Math.random()) {
    foo({p: 123});
    __result1 = obj.p;
} else {
    foo({p: "ABC"});
    __result2 = obj.p;
}
