/*******************************************************************************
    Copyright (c) 2012, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
var x = 5;
try { 
  x;
} finally {
  x = 7;
  try {
    x = 2;
  } finally {
      x = 10;
      try {print(x);}
      finally {x = 9;}
    }
}
x = 12;

