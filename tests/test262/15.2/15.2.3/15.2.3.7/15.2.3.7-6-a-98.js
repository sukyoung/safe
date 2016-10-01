//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     Object.defineProperty(obj, "foo", {
//       value : 200,
//       enumerable : false,
//       writable : true,
//       configurable : true
//     });
//     Object.defineProperties(obj, {
//       foo : {
//         enumerable : true
//       }
//     });
//     return dataPropertyAttributesAreCorrect(obj, "foo", 200, true, true, true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
