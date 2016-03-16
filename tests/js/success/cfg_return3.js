/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
var x;
function f() {
  var y = function g(x) {
    var temp = 1;
    if (y) return;
    else {
      var z = 678;
      print("in");
      var w = function h(t) {
        return temp;
      }
      w(3);
    }
  }
}

f();
