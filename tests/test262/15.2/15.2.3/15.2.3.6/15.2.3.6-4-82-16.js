//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     Object.defineProperty(obj, "0", {
//       value : 1001,
//       writable : true,
//       enumerable : true,
//       configurable : true
//     });
//     Object.defineProperty(obj, "0", {
//       enumerable : true,
//       configurable : false
//     });
//     return dataPropertyAttributesAreCorrect(obj, "0", 1001, true, true, false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
