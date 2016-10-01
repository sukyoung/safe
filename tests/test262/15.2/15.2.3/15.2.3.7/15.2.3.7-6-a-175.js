//   TODO [[DefineOwnProperty]] for Array object
//   function testcase() 
//   {
//     var arr = [0, 1, 2, 3, ];
//     Object.defineProperty(arr, "1", {
//       configurable : false
//     });
//     try
// {      Object.defineProperties(arr, {
//         length : {
//           value : 1
//         }
//       });
//       return false;}
//     catch (e)
// {      return (e instanceof TypeError) && (arr.length === 2);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
