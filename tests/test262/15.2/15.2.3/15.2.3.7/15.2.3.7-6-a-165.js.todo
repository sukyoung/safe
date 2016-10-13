//   TODO [[DefineOwnProperty]] for Array object
//   function testcase() 
//   {
//     var arr = [0, 1, 2, ];
//     Object.defineProperty(arr, "1", {
//       configurable : false
//     });
//     Object.defineProperty(arr, "2", {
//       configurable : true
//     });
//     try
// {      Object.defineProperties(arr, {
//         length : {
//           value : 1
//         }
//       });
//       return false;}
//     catch (e)
// {      return e instanceof TypeError && arr.length === 2 && ! arr.hasOwnProperty("2") && arr[0] === 0 && arr[1] === 1;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
