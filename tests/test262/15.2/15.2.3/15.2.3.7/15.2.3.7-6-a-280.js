//   TODO getter/setter
//   function testcase() 
//   {
//     var arg;
//     (function fun(a, b, c) 
//     {
//       arg = arguments;
//     })(0, 1, 2);
//     function get_func1() 
//     {
//       return 10;
//     }
//     Object.defineProperty(arg, "0", {
//       get : get_func1,
//       enumerable : true,
//       configurable : true
//     });
//     function get_func2() 
//     {
//       return 20;
//     }
//     Object.defineProperties(arg, {
//       "0" : {
//         get : get_func2,
//         enumerable : false,
//         configurable : false
//       }
//     });
//     var desc = Object.getOwnPropertyDescriptor(arg, "0");
//     return desc.get === get_func2 && typeof desc.set === "undefined" && desc.configurable === false && desc.enumerable === false;
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
