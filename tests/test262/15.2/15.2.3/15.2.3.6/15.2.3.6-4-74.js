//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     Object.defineProperty(obj, "foo", {
//       writable : false,
//       configurable : true
//     });
//     Object.defineProperty(obj, "foo", {
//       writable : true
//     });
//     return dataPropertyAttributesAreCorrect(obj, "foo", undefined, true, false, true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
