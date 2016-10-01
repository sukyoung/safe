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
//       enumerable : false,
//       configurable : false
//     });
//     try
// {      Object.defineProperties(arg, {
//         "0" : {
//           configurable : true
//         }
//       });
//       return false;}
//     catch (e)
// {      return (e instanceof TypeError) && dataPropertyAttributesAreCorrect(arg, "0", 0, false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
