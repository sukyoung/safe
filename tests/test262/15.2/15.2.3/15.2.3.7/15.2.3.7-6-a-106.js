//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     function get_func() 
//     {
//       return 10;
//     }
//     Object.defineProperty(obj, "foo", {
//       get : get_func,
//       set : undefined,
//       enumerable : true,
//       configurable : true
//     });
//     function set_func(value) 
//     {
//       obj.setVerifyHelpProp = value;
//     }
//     Object.defineProperties(obj, {
//       foo : {
//         set : set_func
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
