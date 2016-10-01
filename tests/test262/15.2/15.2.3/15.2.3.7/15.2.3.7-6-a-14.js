//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var str = new String();
//     Object.defineProperty(str, "prop", {
//       value : 11,
//       configurable : false
//     });
//     try
// {      Object.defineProperties(str, {
//         prop : {
//           value : 12,
//           configurable : true
//         }
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && dataPropertyAttributesAreCorrect(str, "prop", 11, false, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
