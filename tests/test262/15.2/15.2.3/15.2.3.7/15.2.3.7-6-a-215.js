//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arr = [];
//     Object.defineProperty(arr, "0", {
//       value : 101
//     });
//     try
// {      Object.defineProperties(arr, {
//         "0" : {
//           value : 101
//         }
//       });
//       return dataPropertyAttributesAreCorrect(arr, "0", 101, false, false, false);}
//     catch (e)
// {      return false;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
