//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     try
// {      Object.defineProperty(__Global, "foo", {
//         value : 12,
//         configurable : true
//       });
//       return dataPropertyAttributesAreCorrect(__Global, "foo", 12, false, false, true);}
//     finally
// {      delete __Global.foo;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
