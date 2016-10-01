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
//       enumerable : false
//     });
//     return dataPropertyAttributesAreCorrect(obj, "0", 1001, true, false, true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
