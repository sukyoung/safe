/*******************************************************************************
    Copyright (c) 2012, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
var x, y, z = 1;
try { 
  if (true) x = 2;
  y = {a:"A", b:"B", c:"C"};
  throw y.a;
} catch (e) {
  if (e == "A") undefined;
  try {
    x = function(arg) {
      return arg[0];
    } 
    x([9,6,4,2]);
  } finally {y.d = {test:-23.8};}
} finally {
  while (true) {
    if (x-- == 0) break;
  }
  try {
  } catch (e) {;;;e;;;;;;}
}

