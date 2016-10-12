// TODO getter/setter
//   function testcase() 
//   {
//     var test0 = 0, test1 = 1, test2 = 2;
//     var tokenCodes = {
//       set undefined(value) 
//       {
//         test0 = value;
//       },
//       get undefined()
//       {
//         return test0;
//       },
//       set NaN(value) 
//       {
//         test1 = value;
//       },
//       get NaN()
//       {
//         return test1;
//       },
//       set Infinity(value) 
//       {
//         test2 = value;
//       },
//       get Infinity()
//       {
//         return test2;
//       }
//     };
//     var arr = ['undefined', 'NaN', 'Infinity', ];
//     for(var p in tokenCodes)
//     {
//       for(var p1 in arr)
//       {
//         if (arr[p1] === p)
//         {
//           if (! tokenCodes.hasOwnProperty(arr[p1]))
//           {
//             return false;
//           }
//           ;
//         }
//       }
//     }
//     return true;
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
