//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = [];
//     Object.defineProperty(obj, "0", {
//       value : 1001,
//       writable : true,
//       configurable : false
//     });
//     Object.defineProperty(obj, "0", {
//       value : 1002
//     });
//     return dataPropertyAttributesAreCorrect(obj, "0", 1002, true, false, false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
