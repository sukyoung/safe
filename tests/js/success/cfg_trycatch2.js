/*******************************************************************************
    Copyright (c) 2012, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
var x, y;
x = 5;
try { 
  x = 3;
  try {
    y = 103;
    if (y > x*10) y /= 2;
    else {}
  } catch (y) {
    ;;;;;;;
  }
}
catch (x) {
  x = 2;
}

