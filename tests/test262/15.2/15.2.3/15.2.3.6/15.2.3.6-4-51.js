//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       "property" : 1
//     };
//     Object.defineProperty(obj, "property", {
//       value : 1001,
//       writable : false,
//       enumerable : false,
//       configurable : false
//     });
//     return dataPropertyAttributesAreCorrect(obj, "property", 1001, false, false, false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
