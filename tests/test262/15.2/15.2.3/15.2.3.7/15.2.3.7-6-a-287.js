//   TODO getter/setter
//   function testcase() 
//   {
//     var arg;
//     (function fun(a, b, c) 
//     {
//       arg = arguments;
//     })(0, 1, 2);
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
// {      var desc = Object.getOwnPropertyDescriptor(arg, "0");
//       return e instanceof TypeError && desc.get === get_func && typeof desc.set === "undefined" && desc.enumerable === false && desc.configurable === false;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
