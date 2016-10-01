//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     Object.defineProperty(obj, "foo", {
//       value : 100,
//       enumerable : true,
//       writable : false,
//       configurable : true
//     });
//     Object.defineProperties(obj, {
//       foo : {
//         writable : true
//       }
//     });
//     return dataPropertyAttributesAreCorrect(obj, "foo", 100, true, true, true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
