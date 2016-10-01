//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arrObj = [];
//     Object.defineProperty(arrObj, "1", {
//       value : false
//     });
//     try
// {      Object.defineProperty(arrObj, "1", {
//         value : true
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && dataPropertyAttributesAreCorrect(arrObj, "1", false, false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
