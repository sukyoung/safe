//   TODO getter/setter
//   function testcase() 
//   {
//     var arg;
//     (function fun(a, b, c) 
//     {
//       arg = arguments;
//     })(0, 1, 2);
//     delete arg[0];
//     function get_func() 
//     {
//       return 10;
//     }
//     function set_func(value) 
//     {
//       arg.setVerifyHelpProp = value;
//     }
//     Object.defineProperties(arg, {
//       "0" : {
//         get : get_func,
//         set : set_func,
//         enumerable : true,
//         configurable : true
//       }
//     });
//     return accessorPropertyAttributesAreCorrect(arg, "0", get_func, set_func, "setVerifyHelpProp", true, 
//     true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
