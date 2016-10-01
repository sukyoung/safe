//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arrObj = [];
//     Object.defineProperty(arrObj, "0", {
//       enumerable : false,
//       configurable : true
//     });
//     Object.defineProperty(arrObj, "0", {
//       enumerable : true
//     });
//     return dataPropertyAttributesAreCorrect(arrObj, "0", undefined, false, true, true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
