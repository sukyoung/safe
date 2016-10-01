//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arg = (function () 
//     {
//       return arguments;
//     })(1, 2, 3);
//     Object.defineProperties(arg, {
//       "genericProperty" : {
//         value : 1001,
//         writable : true,
//         enumerable : true,
//         configurable : true
//       }
//     });
//     return dataPropertyAttributesAreCorrect(arg, "genericProperty", 1001, true, true, true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
