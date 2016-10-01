//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arr = [];
//     arr.property = 12;
//     Object.defineProperties(arr, {
//       "property" : {
//         writable : false,
//         enumerable : false,
//         configurable : false
//       }
//     });
//     return dataPropertyAttributesAreCorrect(arr, "property", 12, false, false, false) && arr.length === 0;
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
