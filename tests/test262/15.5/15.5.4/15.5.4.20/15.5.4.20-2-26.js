 function testcase() 
 {
   return String.prototype.trim.call(100000000000000000000.123) === "100000000000000000000";
 }
 {
   var __result1 = testcase();
   var __expect1 = true;
 }
 
