//   TODO [[DefineOwnProperty]] for Array object
//   function testcase() 
//   {
//     var arr = [];
//     arr.length = 3;
//     Object.defineProperties(arr, {
//       "3" : {
//         value : 26
//       }
//     });
//     return arr.length === 4 && arr[3] === 26;
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
