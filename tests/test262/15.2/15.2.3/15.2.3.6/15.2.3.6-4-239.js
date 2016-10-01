//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var arrObj = [];
//     Object.defineProperty(arrObj, "1", {
//       value : 3,
//       writable : true,
//       configurable : false,
//       enumerable : false
//     });
//     try
// {      Object.defineProperty(arrObj, "1", {
//         value : 13,
//         writable : true,
//         enumerable : true
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && dataPropertyAttributesAreCorrect(arrObj, "1", 3, true, false, false);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
