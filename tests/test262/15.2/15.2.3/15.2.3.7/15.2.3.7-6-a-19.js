//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var obj = new RegExp();
//     Object.defineProperty(obj, "prop", {
//       value : 11,
//       configurable : false
//     });
//     try
// {      Object.defineProperties(obj, {
//         prop : {
//           value : 12,
//           configurable : true
//         }
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && dataPropertyAttributesAreCorrect(obj, "prop", 11, false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
