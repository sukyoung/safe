// TODO getter/setter
//   function testcase() 
//   {
//     var test0 = 0, test1 = 1, test2 = 2;
//     var tokenCodes = {
//       set implements(value) 
//       {
//         test0 = value;
//       },
//       get implements()
//       {
//         return test0;
//       },
//       set let(value) 
//       {
//         test1 = value;
//       },
//       get let()
//       {
//         return test1;
//       },
//       set private(value) 
//       {
//         test2 = value;
//       },
//       get private()
//       {
//         return test2;
//       }
//     };
//     var arr = ['implements', 'let', 'private', ];
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
