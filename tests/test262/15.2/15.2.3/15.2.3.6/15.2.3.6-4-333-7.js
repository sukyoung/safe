//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = [];
//     Object.defineProperty(obj, "prop", {
//       value : 1001,
//       writable : true,
//       configurable : false
//     });
//     Object.defineProperty(obj, "prop", {
//       value : 1002
//     });
//     return dataPropertyAttributesAreCorrect(obj, "prop", 1002, true, false, false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
