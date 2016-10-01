//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     function get_func() 
//     {
//       return 0;
//     }
//     function set_func(value) 
//     {
//       obj.setVerifyHelpProp = value;
//     }
//     var desc = {
//       get : get_func,
//       set : set_func,
//       enumerable : true,
//       configurable : true
//     };
//     Object.defineProperty(obj, "foo", desc);
//     Object.defineProperties(obj, {
//       foo : {
//         get : get_func,
//         set : set_func,
//         enumerable : true,
//         configurable : true
//       }
//     });
//     return accessorPropertyAttributesAreCorrect(obj, "foo", get_func, set_func, "setVerifyHelpProp", 
//     true, 
//     true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
