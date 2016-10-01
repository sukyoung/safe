//   TODO [[DefineOwnProperty]] for Array object
//   function testcase() 
//   {
//     var arr = [];
//     var toStringAccessed = false;
//     var valueOfAccessed = false;
//     Object.defineProperties(arr, {
//       length : {
//         value : {
//           toString : (function () 
//           {
//             toStringAccessed = true;
//             return '2';
//           }),
//           valueOf : (function () 
//           {
//             valueOfAccessed = true;
//             return {
//               
//             };
//           })
//         }
//       }
//     });
//     return arr.length === 2 && toStringAccessed && valueOfAccessed;
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
