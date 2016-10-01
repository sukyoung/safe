//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var obj1 = {
//       length : 10
//     };
//     Object.defineProperty(obj, "foo", {
//       value : obj1,
//       writable : false,
//       configurable : false
//     });
//     var obj2 = obj1;
//     obj2.y = "hello";
//     Object.defineProperties(obj, {
//       foo : {
//         value : obj2
//       }
//     });
//     return dataPropertyAttributesAreCorrect(obj, "foo", obj1, false, false, false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
