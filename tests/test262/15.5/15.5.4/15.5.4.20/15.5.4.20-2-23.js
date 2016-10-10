 function testcase() 
 {
   return String.prototype.trim.call(0.00001) === "0.00001";
 }
 {
   var __result1 = testcase();
   var __expect1 = true;
 }
 
