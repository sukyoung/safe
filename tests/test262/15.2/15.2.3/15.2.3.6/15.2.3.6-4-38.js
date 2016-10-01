//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     try
// {      Object.defineProperty(Math, "foo", {
//         value : 12,
//         configurable : true
//       });
//       return dataPropertyAttributesAreCorrect(Math, "foo", 12, false, false, true);}
//     finally
// {      delete Math.foo;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
