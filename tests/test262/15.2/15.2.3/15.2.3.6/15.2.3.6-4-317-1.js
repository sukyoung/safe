//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     return (function (a, b, c) 
//     {
//       Object.defineProperty(arguments, "genericProperty", {
//         value : 1001,
//         writable : false,
//         configurable : false
//       });
//       try
// {        Object.defineProperty(arguments, "genericProperty", {
//           value : 1002
//         });}
//       catch (e)
// {        var verifyFormal = b === 2;
//         return e instanceof TypeError && dataPropertyAttributesAreCorrect(arguments, "genericProperty", 1001, false, false, false) && verifyFormal;}
// 
//       return false;
//     })(1, 2, 3);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
