//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arg = (function () 
//     {
//       return arguments;
//     })(1, 2, 3);
//     Object.defineProperty(arg, "genericProperty", {
//       enumerable : true,
//       configurable : false
//     });
//     try
// {      Object.defineProperties(arg, {
//         "genericProperty" : {
//           enumerable : false
//         }
//       });
//       return false;}
//     catch (ex)
// {      return ex instanceof TypeError && dataPropertyAttributesAreCorrect(arg, "genericProperty", undefined, false, true, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
