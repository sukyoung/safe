//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     Object.defineProperty(obj, "foo", {
//       value : 1001,
//       writable : true,
//       enumerable : true,
//       configurable : true
//     });
//     Object.defineProperty(obj, "foo", {
//       configurable : false
//     });
//     return dataPropertyAttributesAreCorrect(obj, "foo", 1001, true, true, false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
