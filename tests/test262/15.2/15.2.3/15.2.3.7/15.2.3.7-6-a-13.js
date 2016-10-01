//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arr = [];
//     Object.defineProperty(arr, "prop", {
//       value : 11,
//       configurable : false
//     });
//     try
// {      Object.defineProperties(arr, {
//         prop : {
//           value : 12,
//           configurable : true
//         }
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && dataPropertyAttributesAreCorrect(arr, "prop", 11, false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
