//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = __Global;
//     try
// {      Object.defineProperty(obj, "property", {
//         value : 1001,
//         writable : false,
//         configurable : true
//       });
//       Object.defineProperty(obj, "property", {
//         value : 1002
//       });
//       return dataPropertyAttributesAreCorrect(obj, "property", 1002, false, false, true);}
//     finally
// {      delete obj.property;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
