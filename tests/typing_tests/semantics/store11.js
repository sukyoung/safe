/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var x = new Array(16);

for (var i =0; i < 11; i++)
  x[i] = 1;

x[11] = 1;

var __result1 = x.length
var __expect1 = 16;
