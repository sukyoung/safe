//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arrObj = [];
//     Object.defineProperty(arrObj, "1", {
//       value : "abc"
//     });
//     try
// {      Object.defineProperty(arrObj, "1", {
//         value : "fgh"
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && dataPropertyAttributesAreCorrect(arrObj, "1", "abc", false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
