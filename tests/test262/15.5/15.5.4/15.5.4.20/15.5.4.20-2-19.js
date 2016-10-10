 function testcase() 
 {
   return String.prototype.trim.call(0.0000001) === "1e-7";
 }
 {
   var __result1 = testcase();
   var __expect1 = true;
 }
 
