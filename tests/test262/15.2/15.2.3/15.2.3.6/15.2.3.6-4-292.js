//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     return (function () 
//     {
//       Object.defineProperty(arguments, "0", {
//         value : 20,
//         writable : false,
//         enumerable : false,
//         configurable : false
//       });
//       return dataPropertyAttributesAreCorrect(arguments, "0", 20, false, false, false);
//     })(0, 1, 2);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
