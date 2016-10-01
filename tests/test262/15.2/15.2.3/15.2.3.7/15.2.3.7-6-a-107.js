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
//     function set_func(value) 
//     {
//       obj.setVerifyHelpProp = value;
//     }
//     Object.defineProperty(obj, "foo", {
//       get : get_func,
//       set : set_func,
//       enumerable : true,
//       configurable : true
//     });
//     Object.defineProperties(obj, {
//       foo : {
//         enumerable : false
//       }
//     });
//     return accessorPropertyAttributesAreCorrect(obj, "foo", get_func, set_func, "setVerifyHelpProp", 
//     false, 
//     true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
