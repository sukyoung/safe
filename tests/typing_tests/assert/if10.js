/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
var __result1;
var __result2;
var x = (function() { 
var x = __TOP;
function f(){
if(x === 1) {
__result1 = x;
x++;
} else {
__result2 = x;
x++;
}
}
return f;
})()
x()
x()
x()
var __expect1 = 1; // PValue
var __expect2 = __TOP;
