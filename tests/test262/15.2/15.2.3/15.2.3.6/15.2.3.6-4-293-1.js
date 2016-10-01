//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     return (function () 
//     {
//       Object.defineProperty(arguments, "0", {
//         value : 10,
//         writable : false
//       });
//       Object.defineProperty(arguments, "0", {
//         value : 20
//       });
//       return dataPropertyAttributesAreCorrect(arguments, "0", 20, false, true, true);
//     })(0, 1, 2);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
