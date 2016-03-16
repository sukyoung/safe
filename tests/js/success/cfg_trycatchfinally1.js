/*******************************************************************************
    Copyright (c) 2012, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
var x = 1;
try { 
  x = 2;
} catch (x) {
  x = 4;
  try {
    var temp = "hi";
  } finally {}
} finally {
  x = 8;
}
x = 16;

