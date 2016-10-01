//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var getFunc = (function () 
//     {
//       return 1001;
//     });
//     Object.defineProperty(obj, "prop", {
//       get : getFunc,
//       set : undefined,
//       enumerable : false,
//       configurable : true
//     });
//     var result1 = obj.prop === 1001;
//     var desc1 = Object.getOwnPropertyDescriptor(obj, "prop");
//     Object.defineProperty(obj, "prop", {
//       get : undefined
//     });
//     var result2 = typeof obj.prop === "undefined";
//     var desc2 = Object.getOwnPropertyDescriptor(obj, "prop");
//     return result1 && result2 && desc1.get === getFunc && typeof desc2.get === "undefined";
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
