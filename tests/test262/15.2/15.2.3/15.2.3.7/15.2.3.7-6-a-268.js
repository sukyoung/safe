//   TODO getter/setter
//   function testcase() 
//   {
//     var arr = [];
//     function get_fun() 
//     {
//       return 12;
//     }
//     function set_fun(value) 
//     {
//       arr.verifySetFun = value;
//     }
//     Object.defineProperty(arr, "property", {
//       get : (function () 
//       {
//         return 36;
//       }),
//       enumerable : true,
//       configurable : true
//     });
//     Object.defineProperties(arr, {
//       "property" : {
//         get : get_fun,
//         set : set_fun,
//         enumerable : false
//       }
//     });
//     return accessorPropertyAttributesAreCorrect(arr, "property", get_fun, set_fun, "verifySetFun", false, 
//     true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
