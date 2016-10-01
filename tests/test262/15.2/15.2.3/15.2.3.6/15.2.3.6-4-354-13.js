//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = __Global;
//     try
// {      Object.defineProperty(obj, "0", {
//         value : 1001,
//         writable : false,
//         configurable : true
//       });
//       Object.defineProperty(obj, "0", {
//         value : 1002
//       });
//       return dataPropertyAttributesAreCorrect(obj, "0", 1002, false, false, true);}
//     finally
// {      delete obj[0];}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
