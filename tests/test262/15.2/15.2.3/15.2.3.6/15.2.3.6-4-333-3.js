//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = (function () 
//     {
//       return arguments;
//     })();
//     Object.defineProperty(obj, "property", {
//       value : 1001,
//       writable : true,
//       configurable : false
//     });
//     Object.defineProperty(obj, "property", {
//       value : 1002
//     });
//     return dataPropertyAttributesAreCorrect(obj, "property", 1002, true, false, false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
