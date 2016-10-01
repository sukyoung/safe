//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arrObj = [1, ];
//     Object.defineProperty(arrObj, "0", {
//       value : 1001,
//       writable : false,
//       enumerable : false,
//       configurable : false
//     });
//     return dataPropertyAttributesAreCorrect(arrObj, "0", 1001, false, false, false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
