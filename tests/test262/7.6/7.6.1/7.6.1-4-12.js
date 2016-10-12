// TODO getter/setter
//   function testcase() 
//   {
//     var test0 = 0, test1 = 1, test2 = 2;
//     var tokenCodes = {
//       set const(value) 
//       {
//         test0 = value;
//       },
//       get const()
//       {
//         return test0;
//       },
//       set export(value) 
//       {
//         test1 = value;
//       },
//       get export()
//       {
//         return test1;
//       },
//       set import(value) 
//       {
//         test2 = value;
//       },
//       get import()
//       {
//         return test2;
//       }
//     };
//     var arr = ['const', 'export', 'import', ];
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
