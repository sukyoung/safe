//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arg;
//     (function fun() 
//     {
//       arg = arguments;
//     })(0, 1, 2);
//     delete arg[0];
//     Object.defineProperties(arg, {
//       "0" : {
//         value : 10,
//         writable : false,
//         enumerable : false,
//         configurable : false
//       }
//     });
//     return dataPropertyAttributesAreCorrect(arg, "0", 10, false, false, false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
