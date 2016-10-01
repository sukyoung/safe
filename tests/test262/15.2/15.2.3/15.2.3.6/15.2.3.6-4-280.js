//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arrObj = [];
//     arrObj.property = 12;
//     Object.defineProperty(arrObj, "property", {
//       writable : false,
//       enumerable : false,
//       configurable : false
//     });
//     return dataPropertyAttributesAreCorrect(arrObj, "property", 12, false, false, false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
