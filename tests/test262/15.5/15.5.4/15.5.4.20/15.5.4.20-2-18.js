 function testcase() 
 {
   return String.prototype.trim.call(0.000001) === "0.000001";
 }
 {
   var __result1 = testcase();
   var __expect1 = true;
 }
 
