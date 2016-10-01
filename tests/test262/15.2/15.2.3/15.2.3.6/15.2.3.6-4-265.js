//   TODO getter/setter
//   function testcase() 
//   {
//     var arrObj = [];
//     function getFunc() 
//     {
//       return 100;
//     }
//     Object.defineProperty(arrObj, "0", {
//       get : (function () 
//       {
//         return 12;
//       }),
//       configurable : true
//     });
//     Object.defineProperty(arrObj, "0", {
//       get : getFunc
//     });
//     return accessorPropertyAttributesAreCorrect(arrObj, "0", getFunc, undefined, undefined, false, true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
