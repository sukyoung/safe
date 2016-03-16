/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
var x;
function f(x,y,z) {
  var temp = y + z;
  function g(test) {
    test = test + 6;
    return test - x;
  }
  return g(temp);
};
x = f(1,2,3);
