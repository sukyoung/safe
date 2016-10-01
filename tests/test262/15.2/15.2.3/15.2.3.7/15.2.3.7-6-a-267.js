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
//       arr.setVerifyHelpProp = value;
//     }
//     Object.defineProperties(arr, {
//       "property" : {
//         get : get_fun,
//         set : set_fun,
//         enumerable : true,
//         configurable : true
//       }
//     });
//     return accessorPropertyAttributesAreCorrect(arr, "property", get_fun, set_fun, "setVerifyHelpProp", 
//     true, 
//     true) && arr.length === 0;
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
