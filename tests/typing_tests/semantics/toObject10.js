/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

function Class() {this.p = 123}

// toObject(Class)
var x = new Class; 

var __result1 = x.p;
var __expect1 = 123;
