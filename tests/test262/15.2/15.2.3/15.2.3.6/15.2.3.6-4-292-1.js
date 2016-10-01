//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     return (function (a, b, c) 
//     {
//       Object.defineProperty(arguments, "0", {
//         value : 20,
//         writable : false,
//         enumerable : false,
//         configurable : false
//       });
//       var verifyFormal = a === 20;
//       return dataPropertyAttributesAreCorrect(arguments, "0", 20, false, false, false) && verifyFormal;
//     })(0, 1, 2);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
