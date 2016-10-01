//   TODO [[DefineOwnProperty]] for Array object
//   function testcase() 
//   {
//     var arr = [];
//     Object.defineProperties(arr, {
//       "5" : {
//         value : 26
//       }
//     });
//     return arr.length === 6 && arr[5] === 26;
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
