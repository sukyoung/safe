/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var fun = function () {return 1;}

var o = {f:fun};

// toObject(o)
// toObject(f)
var x = o.f(); 

var __result1 = x;
var __expect1 = 1;
