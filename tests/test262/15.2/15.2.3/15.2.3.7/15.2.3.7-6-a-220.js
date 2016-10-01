//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arr = [];
//     Object.defineProperty(arr, "0", {
//       writable : true,
//       configurable : true
//     });
//     try
// {      Object.defineProperties(arr, {
//         "0" : {
//           writable : false
//         }
//       });
//       return dataPropertyAttributesAreCorrect(arr, "0", undefined, false, false, true);}
//     catch (e)
// {      return false;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
