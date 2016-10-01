//   TODO getter/setter
//   function testcase() 
//   {
//     var arg;
//     (function fun() 
//     {
//       arg = arguments;
//     })();
//     function get_func() 
//     {
//       return 0;
//     }
//     Object.defineProperty(arg, "0", {
//       get : get_func,
//       set : undefined,
//       enumerable : false,
//       configurable : false
//     });
//     function set_func(value) 
//     {
//       arg.setVerifyHelpProp = value;
//     }
//     try
// {      Object.defineProperties(arg, {
//         "0" : {
//           set : set_func
//         }
//       });
//       return false;}
//     catch (e)
// {      return (e instanceof TypeError) && accessorPropertyAttributesAreCorrect(arg, "0", get_func, undefined, undefined, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
