//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var desc = {
//       writable : false,
//       configurable : true
//     };
//     Object.defineProperty(obj, "foo", desc);
//     Object.defineProperties(obj, {
//       foo : {
//         writable : true,
//         configurable : true
//       }
//     });
//     return dataPropertyAttributesAreCorrect(obj, "foo", undefined, true, false, true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
