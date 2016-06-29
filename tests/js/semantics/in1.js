/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var o = {x:123};

var __result1 = "x" in o; 
var __expect1 = true;

var __result2 = "y" in o; 
var __expect2 = false;
