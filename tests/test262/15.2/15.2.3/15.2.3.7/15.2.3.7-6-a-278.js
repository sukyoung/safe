//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arg;
//     (function fun(a, b, c) 
//     {
//       arg = arguments;
//     })(0, 1, 2);
//     delete arg[0];
//     Object.defineProperties(arg, {
//       "0" : {
//         value : 10,
//         writable : true,
//         enumerable : true,
//         configurable : true
//       }
//     });
//     return dataPropertyAttributesAreCorrect(arg, "0", 10, true, true, true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
