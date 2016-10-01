//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     Object.defineProperty(obj, "foo", {
//       value : "abcd",
//       writable : false,
//       configurable : false
//     });
//     Object.defineProperties(obj, {
//       foo : {
//         value : "abcd"
//       }
//     });
//     return dataPropertyAttributesAreCorrect(obj, "foo", "abcd", false, false, false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
