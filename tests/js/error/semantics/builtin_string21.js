/*******************************************************************************
Copyright (c) 2012, S-Core.
All rights reserved.

Use is subject to license terms.

This distribution may include materials developed by third parties.
******************************************************************************/

// to test monotonicity of def/use function.

var x = new String ("123");

var __result1 = x.concat("4");
var __expect1 = "1234"
	
var __result2 = x.concat("5", "6"); 
var __expect2 = "12356";
