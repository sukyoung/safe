//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arrObj = [100, ];
//     Object.defineProperty(arrObj, "0", {
//       writable : false,
//       enumerable : false,
//       configurable : false
//     });
//     return dataPropertyAttributesAreCorrect(arrObj, "0", 100, false, false, false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
