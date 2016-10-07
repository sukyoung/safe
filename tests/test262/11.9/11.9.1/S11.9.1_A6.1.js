// TODO eval: statement
// //CHECK#1
// if ((undefined == undefined) !== true) {
//   $ERROR('#1: (undefined == undefined) === true');
// }

// //CHECK#2
// if ((void 0 == undefined) !== true) {
//   $ERROR('#2: (void 0 == undefined) === true');
// }

// //CHECK#3
// if ((undefined == eval("var x")) !== true) {
//   $ERROR('#3: (undefined == eval("var x")) === true');
// }

// //CHECK#4
// if ((undefined == null) !== true) {
//   $ERROR('#4: (undefined == null) === true');
// }

// //CHECK#5
// if ((null == void 0) !== true) {
//   $ERROR('#5: (null == void 0) === true');
// }

// //CHECK#6
// if ((null == null) !== true) {
//   $ERROR('#6: (null == null) === true');
// }

