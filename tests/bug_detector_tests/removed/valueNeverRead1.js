/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = 3;
function Foo() {this.p = 1}
Foo.prototype.valueOf = undefined;
Foo.prototype.toString = undefined;
var y = new Foo();
x+y;
