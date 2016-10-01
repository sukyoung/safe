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
//       enumerable : true,
//       configurable : false
//     });
//     try
// {      Object.defineProperties(arg, {
//         "0" : {
//           enumerable : false
//         }
//       });
//       return false;}
//     catch (e)
// {      return (e instanceof TypeError) && accessorPropertyAttributesAreCorrect(arg, "0", get_func, undefined, undefined, true, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
