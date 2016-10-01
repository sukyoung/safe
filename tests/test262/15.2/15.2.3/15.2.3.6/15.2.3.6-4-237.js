//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arrObj = [];
//     Object.defineProperty(arrObj, "0", {
//       configurable : true
//     });
//     Object.defineProperty(arrObj, "0", {
//       configurable : false
//     });
//     return dataPropertyAttributesAreCorrect(arrObj, "0", undefined, false, false, false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
