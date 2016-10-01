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
//     function get_func2() 
//     {
//       return 20;
//     }
//     Object.defineProperties(obj, {
//       foo : {
//         get : get_func2
//       }
//     });
//     return accessorPropertyAttributesAreCorrect(obj, "foo", get_func2, set_func, "setVerifyHelpProp", 
//     true, 
//     true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
