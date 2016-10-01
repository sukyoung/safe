//   TODO getter/setter
//   function testcase() 
//   {
//     var arr = [];
//     Object.defineProperty(arr, "0", {
//       get : (function () 
//       {
//         return 11;
//       }),
//       configurable : false
//     });
//     try
// {      Object.defineProperties(arr, {
//         "0" : {
//           get : (function () 
//           {
//             return 12;
//           }),
//           configurable : true
//         }
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && arr[0] === 11;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
