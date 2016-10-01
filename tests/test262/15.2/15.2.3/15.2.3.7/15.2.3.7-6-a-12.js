//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var fun = (function () 
//     {
//       
//     });
//     Object.defineProperty(fun, "prop", {
//       value : 11,
//       configurable : false
//     });
//     try
// {      Object.defineProperties(fun, {
//         prop : {
//           value : 12,
//           configurable : true
//         }
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && dataPropertyAttributesAreCorrect(fun, "prop", 11, false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
