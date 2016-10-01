//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var globalVariable = 20;
//     var getFunc = (function () 
//     {
//       globalVariable = 2010;
//       return globalVariable;
//     });
//     Object.defineProperty(obj, "prop", {
//       get : getFunc
//     });
//     var desc = Object.getOwnPropertyDescriptor(obj, "prop");
//     return obj.hasOwnProperty("prop") && desc.get === getFunc && obj.prop === 2010 && globalVariable === 2010;
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
