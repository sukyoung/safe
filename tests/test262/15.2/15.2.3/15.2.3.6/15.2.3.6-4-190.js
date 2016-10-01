//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arrObj = [];
//     Object.defineProperty(arrObj, 0, {
//       value : "ownDataProperty",
//       configurable : false
//     });
//     try
// {      Object.defineProperty(arrObj, 0, {
//         configurable : true
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && dataPropertyAttributesAreCorrect(arrObj, "0", "ownDataProperty", false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
