/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var x;

switch (5) {
	case 1 : 
		x = 1; 
	case 2 :
		x = 2;
    case 3 : 
    	x = 3;
    	break;
    default :
    	x = -1;
    case 4 :
    	x = 4;
    	break;
}

var __result1 = x;
var __expect1 = 4;


switch (1) {
	case 1 : 
		x = 1; 
	case 2 :
		x = 2;
    case 3 : 
    	x = 3;
    	break;
    default :
    	x = -1;
    case 4 :
    	x = 4;
    	break;
}

var __result2 = x;
var __expect2 = 3;
