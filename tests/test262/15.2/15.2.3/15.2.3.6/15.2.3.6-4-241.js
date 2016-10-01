// TODO rewrite dataPropertyAttributesAreCorrect
// //   TODO getter/setter
// //   function testcase() 
// //   {
// //     var arrObj = [];
// //     Object.defineProperty(arrObj, "1", {
// //       value : 3,
// //       configurable : false
// //     });
// //     try
// // {      Object.defineProperty(arrObj, "1", {
// //         set : (function () 
// //         {
// //           
// //         })
// //       });
// //       return false;}
// //     catch (e)
// // {      return e instanceof TypeError && dataPropertyAttributesAreCorrect(arrObj, "1", 3, false, false, false);}
// // 
// //   }
// //   {
// //     var __result1 = testcase();
// //     var __expect1 = true;
// //   }
// //   
