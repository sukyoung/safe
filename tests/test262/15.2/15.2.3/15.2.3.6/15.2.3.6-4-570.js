//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var verifyExecute = false;
//     var getFunc = (function () 
//     {
//       verifyExecute = true;
//     });
//     Object.defineProperty(obj, "prop", {
//       get : getFunc
//     });
//     var desc = Object.getOwnPropertyDescriptor(obj, "prop");
//     return obj.hasOwnProperty("prop") && desc.get === getFunc && typeof obj.prop === "undefined" && verifyExecute;
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
