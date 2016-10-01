//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arr = [];
//     Object.defineProperty(arr, "1", {
//       value : "abcd"
//     });
//     try
// {      Object.defineProperties(arr, {
//         "1" : {
//           value : "efgh"
//         }
//       });
//       return false;}
//     catch (ex)
// {      return (ex instanceof TypeError) && dataPropertyAttributesAreCorrect(arr, "1", "abcd", false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
