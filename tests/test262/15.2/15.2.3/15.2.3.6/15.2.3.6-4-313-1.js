//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     return (function (a, b, c) 
//     {
//       Object.defineProperty(arguments, "genericProperty", {
//         value : 1001,
//         writable : true,
//         enumerable : true,
//         configurable : true
//       });
//       return dataPropertyAttributesAreCorrect(arguments, "genericProperty", 1001, true, true, true);
//     })(1, 2, 3);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
