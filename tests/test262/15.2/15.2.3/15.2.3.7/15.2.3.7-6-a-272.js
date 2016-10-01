//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arr = [];
//     Object.defineProperty(arr, "property", {
//       value : 12,
//       enumerable : false
//     });
//     try
// {      Object.defineProperties(arr, {
//         "property" : {
//           enumerable : true
//         }
//       });
//       return false;}
//     catch (ex)
// {      return (ex instanceof TypeError) && dataPropertyAttributesAreCorrect(arr, "property", 12, false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
