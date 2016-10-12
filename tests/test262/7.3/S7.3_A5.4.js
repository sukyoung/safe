// TODO eval: comment
// // CHECK#1
// eval("/*\u2029 multi line \u2029 comment \u2029*/");

// //CHECK#2
// var x = 0;
// eval("/*\u2029 multi line \u2029 comment \u2029 x = 1;*/");
// if (x !== 0) {
//   $ERROR('#1: var x = 0; eval("/*\\u2029 multi line \\u2029 comment \\u2029 x = 1;*/"); x === 0. Actual: ' + (x));
// }

