//   TODO [[DefineOwnProperty]] for Array object
//   function testcase() 
//   {
//     var arr = [0, 1, ];
//     try
// {      Array.prototype[1] = 2;
//       Object.defineProperties(arr, {
//         length : {
//           value : 1
//         }
//       });
//       return arr.length === 1 && ! arr.hasOwnProperty("1") && arr[0] === 0 && Array.prototype[1] === 2;}
//     finally
// {      delete Array.prototype[1];}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
