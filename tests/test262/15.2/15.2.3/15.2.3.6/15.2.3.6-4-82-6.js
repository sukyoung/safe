//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     Object.defineProperty(obj, "foo", {
//       value : 1001,
//       writable : true,
//       enumerable : false,
//       configurable : true
//     });
//     Object.defineProperty(obj, "foo", {
//       enumerable : true
//     });
//     return dataPropertyAttributesAreCorrect(obj, "foo", 1001, true, true, true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
