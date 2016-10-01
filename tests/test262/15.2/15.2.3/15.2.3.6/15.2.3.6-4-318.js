//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     return (function () 
//     {
//       Object.defineProperty(arguments, "genericProperty", {
//         writable : false,
//         configurable : false
//       });
//       try
// {        Object.defineProperty(arguments, "genericProperty", {
//           writable : true
//         });}
//       catch (e)
// {        return e instanceof TypeError && dataPropertyAttributesAreCorrect(arguments, "genericProperty", undefined, false, false, 
//         false);}
// 
//       return false;
//     })(1, 2, 3);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
