//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = [];
//     Object.defineProperty(obj, "prop", {
//       value : 1001,
//       writable : false,
//       configurable : true
//     });
//     Object.defineProperty(obj, "prop", {
//       value : 1002
//     });
//     return dataPropertyAttributesAreCorrect(obj, "prop", 1002, false, false, true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
