/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var o1 = { };
o1.x = 10;
o1.f1 = function () { this.f2(); };
o1.f2 = function () { };

var x = 20;
o1.f1();

var __result1 = this.x;
var __expect1 = 20;
