//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arrObj = [];
//     Object.defineProperty(arrObj, "0", {
//       writable : false,
//       configurable : true
//     });
//     Object.defineProperty(arrObj, "0", {
//       writable : true
//     });
//     return dataPropertyAttributesAreCorrect(arrObj, "0", undefined, true, false, true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
