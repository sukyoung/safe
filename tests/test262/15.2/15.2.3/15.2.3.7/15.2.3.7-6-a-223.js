//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arr = [];
//     Object.defineProperty(arr, "0", {
//       enumerable : true
//     });
//     try
// {      Object.defineProperties(arr, {
//         "0" : {
//           enumerable : true
//         }
//       });
//       return dataPropertyAttributesAreCorrect(arr, "0", undefined, false, true, false);}
//     catch (e)
// {      return false;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
