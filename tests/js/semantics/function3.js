/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

function Foo() { };
Foo.prototype.x = 10;
Foo.prototype = 20;

var __result1 = Foo.prototype;
var __expect1 = 20;
