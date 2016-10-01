//   TODO getter/setter
//   function testcase() 
//   {
//     var arr = [];
//     function get_fun() 
//     {
//       return 36;
//     }
//     Object.defineProperty(arr, "0", {
//       get : undefined,
//       configurable : true
//     });
//     Object.defineProperties(arr, {
//       "0" : {
//         get : get_fun
//       }
//     });
//     return accessorPropertyAttributesAreCorrect(arr, "0", get_fun, undefined, undefined, false, true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
