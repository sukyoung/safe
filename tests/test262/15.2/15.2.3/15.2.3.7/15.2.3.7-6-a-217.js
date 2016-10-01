//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arr = [];
//     Object.defineProperty(arr, "0", {
//       value : true
//     });
//     try
// {      Object.defineProperties(arr, {
//         "0" : {
//           value : true
//         }
//       });
//       return dataPropertyAttributesAreCorrect(arr, "0", true, false, false, false);}
//     catch (e)
// {      return false;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
