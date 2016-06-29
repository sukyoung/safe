/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var o1 = {p1:123, p2:456};

var __result1 = o1[__TOP]; 
var __expect1 = 123;

var __result2 = o1[__TOP]; 
var __expect2 = 456;
