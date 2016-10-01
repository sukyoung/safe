//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arg = (function () 
//     {
//       return arguments;
//     })(1, 2, 3);
//     Object.defineProperty(arg, "genericProperty", {
//       value : 1001,
//       writable : true,
//       enumerable : true,
//       configurable : true
//     });
//     Object.defineProperties(arg, {
//       "genericProperty" : {
//         value : 1002,
//         enumerable : false,
//         configurable : false
//       }
//     });
//     return dataPropertyAttributesAreCorrect(arg, "genericProperty", 1002, true, false, false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
