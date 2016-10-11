// TODO strict
// function testcase() {
//         try {
//             var obj = {};
//             Object.defineProperty(obj, "accProperty", {
//                 get: function () {
//                     "use strict";
//                     eval("var public = 1;");
//                     return 11;
//                 }
//             });
//             var temp = obj.accProperty === 11;
//             return false;
//         } catch (e) {
//             return e instanceof SyntaxError;
//         }
//     }
// runTestCase(testcase);
