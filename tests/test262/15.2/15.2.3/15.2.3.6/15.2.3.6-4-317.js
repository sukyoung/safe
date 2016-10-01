//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     return (function () 
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
// {        return e instanceof TypeError && dataPropertyAttributesAreCorrect(arguments, "genericProperty", 1001, false, false, false);}
// 
//       return false;
//     })(1, 2, 3);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
