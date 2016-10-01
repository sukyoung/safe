//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arrObj = [];
//     arrObj[0] = 100;
//     Object.defineProperty(arrObj, "0", {
//       value : 100,
//       writable : true,
//       enumerable : true,
//       configurable : true
//     });
//     return dataPropertyAttributesAreCorrect(arrObj, "0", 100, true, true, true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
