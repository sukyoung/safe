//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     try
// {      Object.defineProperty(JSON, "foo", {
//         value : 12,
//         configurable : true
//       });
//       return dataPropertyAttributesAreCorrect(JSON, "foo", 12, false, false, true);}
//     finally
// {      delete JSON.foo;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
