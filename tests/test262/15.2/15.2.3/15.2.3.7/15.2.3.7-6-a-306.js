//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arg = (function () 
//     {
//       return arguments;
//     })(1, 2, 3);
//     Object.defineProperty(arg, "genericProperty", {
//       value : 1001,
//       writable : false,
//       configurable : false
//     });
//     try
// {      Object.defineProperties(arg, {
//         "genericProperty" : {
//           value : 1002
//         }
//       });
//       return false;}
//     catch (ex)
// {      return ex instanceof TypeError && dataPropertyAttributesAreCorrect(arg, "genericProperty", 1001, false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
