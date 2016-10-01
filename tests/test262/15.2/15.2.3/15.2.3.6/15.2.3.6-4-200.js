//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arrObj = [];
//     Object.defineProperty(arrObj, "0", {
//       writable : true,
//       enumerable : true,
//       configurable : false
//     });
//     return dataPropertyAttributesAreCorrect(arrObj, "0", undefined, true, true, false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
