//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arr = [];
//     Object.defineProperty(arr, "0", {
//       configurable : true
//     });
//     try
// {      Object.defineProperties(arr, {
//         "0" : {
//           configurable : false
//         }
//       });
//       return dataPropertyAttributesAreCorrect(arr, "0", undefined, false, false, false);}
//     catch (e)
// {      return false;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
