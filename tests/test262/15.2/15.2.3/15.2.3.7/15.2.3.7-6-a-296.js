//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arg;
//     (function fun() 
//     {
//       arg = arguments;
//     })();
//     Object.defineProperty(arg, "0", {
//       value : 0,
//       writable : false,
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
// {      return (e instanceof TypeError) && dataPropertyAttributesAreCorrect(arg, "0", 0, false, true, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
