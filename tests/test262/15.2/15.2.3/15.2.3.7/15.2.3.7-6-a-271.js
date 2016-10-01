//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arr = [];
//     Object.defineProperty(arr, "property", {
//       writable : false
//     });
//     try
// {      Object.defineProperties(arr, {
//         "property" : {
//           writable : true
//         }
//       });
//       return false;}
//     catch (ex)
// {      return (ex instanceof TypeError) && dataPropertyAttributesAreCorrect(arr, "property", undefined, false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
