// TODO [[DefineOwnProperty]] for Array object
// var x = [0, 1, 2, ];
// x[4294967294] = 4294967294;
// x.length = 2;
// {
//   var __result1 = x[0] !== 0;
//   var __expect1 = false;
// }
// {
//   var __result2 = x[1] !== 1;
//   var __expect2 = false;
// }
// {
//   var __result3 = x[2] !== undefined;
//   var __expect3 = false;
// }
// {
//   var __result4 = x[4294967294] !== undefined;
//   var __expect4 = false;
// }
