 function testcase() 
 {
   return String.prototype.trim.call(10000000000000000000000) === "1e+22";
 }
 {
   var __result1 = testcase();
   var __expect1 = true;
 }
 
