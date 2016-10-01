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
//       var arr = [];
//       Object.defineProperty(arr, "0", {
//         value : 12,
//         configurable : false
//       });
//       Object.defineProperties(arr, {
//         "0" : {
//           configurable : true
//         }
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && arr[0] === 12 && Array.prototype[0] === 11;}
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
