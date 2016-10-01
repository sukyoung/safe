//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = [];
//     Object.defineProperty(obj, "0", {
//       value : 1001,
//       writable : false,
//       configurable : true
//     });
//     Object.defineProperty(obj, "0", {
//       value : 1002
//     });
//     return dataPropertyAttributesAreCorrect(obj, "0", 1002, false, false, true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
