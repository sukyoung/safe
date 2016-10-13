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
//       return 0;
//     }
//     Object.defineProperty(arg, "0", {
//       get : get_func1,
//       enumerable : false,
//       configurable : false
//     });
//     function get_func2() 
//     {
//       return 10;
//     }
//     try
// {      Object.defineProperties(arg, {
//         "0" : {
//           get : get_func2
//         }
//       });
//       return false;}
//     catch (e)
// {      var desc = Object.getOwnPropertyDescriptor(arg, "0");
//       return e instanceof TypeError && desc.get === get_func1 && typeof desc.set === "undefined" && desc.enumerable === false && desc.configurable === false;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
