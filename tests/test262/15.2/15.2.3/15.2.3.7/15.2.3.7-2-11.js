//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var result = false;
//     try
// {      Object.defineProperty(Math, "prop", {
//         get : (function () 
//         {
//           result = (this === Math);
//           return {
//             
//           };
//         }),
//         enumerable : true,
//         configurable : true
//       });
//       Object.defineProperties(obj, Math);
//       return result;}
//     finally
// {      delete Math.prop;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
