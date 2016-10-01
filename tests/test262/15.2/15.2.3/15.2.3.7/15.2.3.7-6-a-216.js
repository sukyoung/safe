//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arr = [];
//     Object.defineProperty(arr, "0", {
//       value : "abcd"
//     });
//     try
// {      Object.defineProperties(arr, {
//         "0" : {
//           value : "abcd"
//         }
//       });
//       return dataPropertyAttributesAreCorrect(arr, "0", "abcd", false, false, false);}
//     catch (e)
// {      return false;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
