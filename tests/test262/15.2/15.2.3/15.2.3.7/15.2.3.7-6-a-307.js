//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arg = (function () 
//     {
//       return arguments;
//     })(1, 2, 3);
//     Object.defineProperty(arg, "genericProperty", {
//       writable : false,
//       configurable : false
//     });
//     try
// {      Object.defineProperties(arg, {
//         "genericProperty" : {
//           writable : true
//         }
//       });
//       return false;}
//     catch (ex)
// {      return ex instanceof TypeError && dataPropertyAttributesAreCorrect(arg, "genericProperty", undefined, false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
