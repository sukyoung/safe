//   TODO getter/setter
//   function testcase() 
//   {
//     var arr = [];
//     function get_func() 
//     {
//       return 10;
//     }
//     Object.defineProperty(arr, "0", {
//       get : get_func
//     });
//     Object.defineProperties(arr, {
//       "0" : {
//         get : get_func
//       }
//     });
//     return accessorPropertyAttributesAreCorrect(arr, "0", get_func, undefined, undefined, false, false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
