// TODO eval: statement
// // CHECK#1
// eval("\u000Dvar\u000Dx\u000D=\u000D1\u000D");
// if (x !== 1) {
//   $ERROR('#1: eval("\\u000Dvar\\u000Dx\\u000D=\\u000D1\\u000D"); x === 1. Actual: ' + (x));
// }

// //CHECK#2
// eval("\u000D" + "var" + "\u000D" + "x" + "\u000D" + "=" + "\u000D" + "1" + "\u000D");
// if (x !== 1) {
//   $ERROR('#2: eval("\\u000D" + "var" + "\\u000D" + "x" + "\\u000D" + "=" + "\\u000D" + "1" + "\\u000D"); x === 1. Actual: ' + (x));
// }

// //CHECK#3
// eval("\rvar\rx\r=\r1\r");
// if (x !== 1) {
//   $ERROR('#3: eval("\\rvar\\rx\\r=\\r1\\r"); x === 1. Actual: ' + (x));
// }

// //CHECK#4
// eval("\r" + "var" + "\r" + "x" + "\r" + "=" + "\r" + "1" + "\r");
// if (x !== 1) {
//   $ERROR('#4: eval("\\r" + "var" + "\\r" + "x" + "\\r" + "=" + "\\r" + "1" + "\\r"); x === 1. Actual: ' + (x));
// }

// //CHECK#5
// eval("\u000D" + "var" + "\r" + "x" + "\u000D" + "=" + "\r" + "1" + "\u000D");
// if (x !== 1) {
//   $ERROR('#5: eval("\\u000D" + "var" + "\\r" + "x" + "\\u000D" + "=" + "\\r" + "1" + "\\u000D"); x === 1. Actual: ' + (x));
// }

