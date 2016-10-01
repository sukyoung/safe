//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arg;
//     (function fun(a, b, c) 
//     {
//       arg = arguments;
//     })(0, 1, 2);
//     Object.defineProperties(arg, {
//       "0" : {
//         value : 20,
//         writable : false,
//         enumerable : false,
//         configurable : false
//       }
//     });
//     return dataPropertyAttributesAreCorrect(arg, "0", 20, false, false, false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
