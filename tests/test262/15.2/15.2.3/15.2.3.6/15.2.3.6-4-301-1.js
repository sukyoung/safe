//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     return (function (a, b, c) 
//     {
//       delete arguments[0];
//       Object.defineProperty(arguments, "0", {
//         value : 10,
//         writable : false,
//         enumerable : false,
//         configurable : false
//       });
//       var verifyFormal = a === 0;
//       return dataPropertyAttributesAreCorrect(arguments, "0", 10, false, false, false) && verifyFormal;
//     })(0, 1, 2);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
