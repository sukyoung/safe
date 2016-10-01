//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arrObj = [];
//     var obj = {
//       length : 10
//     };
//     Object.defineProperty(arrObj, "1", {
//       value : obj
//     });
//     try
// {      Object.defineProperty(arrObj, "1", {
//         value : {
//           
//         }
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && dataPropertyAttributesAreCorrect(arrObj, "1", obj, false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
