//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     Object.defineProperty(obj, "foo", {
//       value : 100,
//       writable : true,
//       configurable : true
//     });
//     Object.defineProperties(obj, {
//       foo : {
//         value : 200,
//         writable : false,
//         configurable : false
//       }
//     });
//     return dataPropertyAttributesAreCorrect(obj, "foo", 200, false, false, false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
