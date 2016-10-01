//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arrObj = [];
//     Object.defineProperty(arrObj, "property", {
//       value : 12
//     });
//     try
// {      Object.defineProperty(arrObj, "property", {
//         configurable : true
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && dataPropertyAttributesAreCorrect(arrObj, "property", 12, false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
