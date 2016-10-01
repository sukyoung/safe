//   TODO getter/setter
//   function testcase() 
//   {
//     var arg = (function () 
//     {
//       return arguments;
//     })(1, 2, 3);
//     var accessed = false;
//     Object.defineProperties(arg, {
//       "0" : {
//         get : (function () 
//         {
//           accessed = true;
//           return 12;
//         })
//       }
//     });
//     return arg[0] === 12 && accessed;
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
