//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     Object.defineProperty(obj, "foo", {
//       enumerable : false,
//       configurable : true
//     });
//     Object.defineProperty(obj, "foo", {
//       enumerable : true
//     });
//     return dataPropertyAttributesAreCorrect(obj, "foo", undefined, false, true, true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
