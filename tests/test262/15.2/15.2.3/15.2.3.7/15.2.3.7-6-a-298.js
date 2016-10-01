//   TODO getter/setter
//   function testcase() 
//   {
//     var arg;
//     (function fun() 
//     {
//       arg = arguments;
//     })();
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
// {      return (e instanceof TypeError) && accessorPropertyAttributesAreCorrect(arg, "0", get_func1, undefined, undefined, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
