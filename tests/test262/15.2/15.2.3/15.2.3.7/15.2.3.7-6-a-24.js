//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     try
// {      Object.defineProperty(fnGlobalObject(), "prop", {
//         value : 11,
//         writable : true,
//         enumerable : true,
//         configurable : true
//       });
//       Object.defineProperties(fnGlobalObject(), {
//         prop : {
//           value : 12
//         }
//       });
//       return dataPropertyAttributesAreCorrect(fnGlobalObject(), "prop", 12, true, true, true);}
//     finally
// {      delete fnGlobalObject().prop;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
