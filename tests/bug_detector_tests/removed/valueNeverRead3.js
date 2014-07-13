/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = 10;

function f() {
  x = 7;
}

var g = 20;

g = function () {
  var t = x;
}

g = 30;
