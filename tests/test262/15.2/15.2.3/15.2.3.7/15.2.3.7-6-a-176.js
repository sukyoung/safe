//   TODO [[DefineOwnProperty]] for Array object
//   function testcase() 
//   {
//     var arr = [0, 1, ];
//     try
// {      Object.defineProperty(arr, "1", {
//         configurable : false
//       });
//       Object.defineProperties(arr, {
//         length : {
//           value : 1,
//           writable : false
//         }
//       });
//       return false;}
//     catch (e)
// {      arr.length = 10;
//       return e instanceof TypeError && arr.hasOwnProperty("1") && arr.length === 2 && arr[0] === 0 && arr[1] === 1;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
