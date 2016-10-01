//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arr = [];
//     Object.defineProperty(arr, "0", {
//       value : NaN
//     });
//     Object.defineProperties(arr, {
//       "0" : {
//         value : NaN
//       }
//     });
//     return dataPropertyAttributesAreCorrect(arr, "0", NaN, false, false, false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
