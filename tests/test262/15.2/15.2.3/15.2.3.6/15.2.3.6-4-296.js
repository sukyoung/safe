//   TODO rewrite dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     return (function () 
//     {
//       Object.defineProperty(arguments, "0", {
//         value : 10,
//         writable : false,
//         enumerable : false,
//         configurable : false
//       });
//       try
// {        Object.defineProperty(arguments, "0", {
//           configurable : true
//         });}
//       catch (e)
// {        return e instanceof TypeError && dataPropertyAttributesAreCorrect(arguments, "0", 10, false, false, false);}
// 
//       return false;
//     })(0, 1, 2);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
