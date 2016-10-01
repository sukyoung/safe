//   TODO getter/setter
//   function testcase() 
//   {
//     try
// {      Object.defineProperty(Array.prototype, "0", {
//         get : (function () 
//         {
//           return 11;
//         }),
//         configurable : true
//       });
//       var arrObj = [];
//       Object.defineProperty(arrObj, "0", {
//         value : 12,
//         configurable : false
//       });
//       Object.defineProperty(arrObj, "0", {
//         configurable : true
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && Array.prototype[0] === 11 && arrObj[0] === 12;}
// 
//     finally
// {      delete Array.prototype[0];}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
