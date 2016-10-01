//   TODO getter/setter
//   function testcase() 
//   {
//     var arr = [];
//     Object.defineProperty(arr, "0", {
//       get : (function () 
//       {
//         return 12;
//       }),
//       configurable : true
//     });
//     Object.defineProperties(arr, {
//       "0" : {
//         get : undefined
//       }
//     });
//     return accessorPropertyAttributesAreCorrect(arr, "0", undefined, undefined, undefined, false, true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
