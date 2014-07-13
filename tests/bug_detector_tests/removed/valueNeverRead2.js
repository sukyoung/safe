/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = 3;
var y = x + 7;
var o = {};
o.p = "property";
if (y < x) x = 9;
else o.q = 1000;
o = x;
