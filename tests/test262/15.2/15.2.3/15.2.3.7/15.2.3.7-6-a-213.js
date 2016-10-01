//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arr = [];
//     Object.defineProperty(arr, "0", {
//       value : - 0
//     });
//     try
// {      Object.defineProperties(arr, {
//         "0" : {
//           value : + 0
//         }
//       });
//       return false;}
//     catch (e)
// {      return (e instanceof TypeError) && dataPropertyAttributesAreCorrect(arr, "0", - 0, false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
