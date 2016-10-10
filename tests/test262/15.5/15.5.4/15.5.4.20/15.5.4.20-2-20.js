 function testcase() 
 {
   return String.prototype.trim.call(0.00000001) === "1e-8";
 }
 {
   var __result1 = testcase();
   var __expect1 = true;
 }
 
