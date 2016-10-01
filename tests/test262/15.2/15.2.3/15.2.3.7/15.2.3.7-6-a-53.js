//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var obj1 = {
//       length : 10
//     };
//     var desc = {
//       value : obj1
//     };
//     Object.defineProperty(obj, "foo", desc);
//     Object.defineProperties(obj, {
//       foo : {
//         value : obj1
//       }
//     });
//     return dataPropertyAttributesAreCorrect(obj, "foo", obj1, false, false, false);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
