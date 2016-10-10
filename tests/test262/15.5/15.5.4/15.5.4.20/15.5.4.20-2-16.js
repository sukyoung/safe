 function testcase() 
 {
   return String.prototype.trim.call(1000000000000000000000) === "1e+21";
 }
 {
   var __result1 = testcase();
   var __expect1 = true;
 }
 
