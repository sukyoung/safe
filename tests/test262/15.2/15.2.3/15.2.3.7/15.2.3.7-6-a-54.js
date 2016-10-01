//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var obj1 = {
//       length : 10
//     };
//     obj.foo = obj1;
//     var obj2 = {
//       length : 20
//     };
//     Object.defineProperties(obj, {
//       foo : {
//         value : obj2
//       }
//     });
//     return dataPropertyAttributesAreCorrect(obj, "foo", obj2, true, true, true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
