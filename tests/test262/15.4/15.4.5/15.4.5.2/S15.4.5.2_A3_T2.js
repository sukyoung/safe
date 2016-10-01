// TODO [[DefineOwnProperty]] for Array object
// var x = [];
// x[1] = 1;
// x[3] = 3;
// x[5] = 5;
// x.length = 4;
// {
//   var __result1 = x.length !== 4;
//   var __expect1 = false;
// }
// {
//   var __result2 = x[5] !== undefined;
//   var __expect2 = false;
// }
// {
//   var __result3 = x[3] !== 3;
//   var __expect3 = false;
// }
// x.length = new Number(6);
// {
//   var __result4 = x[5] !== undefined;
//   var __expect4 = false;
// }
// x.length = 0;
// {
//   var __result5 = x[0] !== undefined;
//   var __expect5 = false;
// }
// x.length = 1;
// {
//   var __result6 = x[1] !== undefined;
//   var __expect6 = false;
// }
