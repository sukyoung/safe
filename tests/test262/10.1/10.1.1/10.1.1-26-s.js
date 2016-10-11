// TODO strict
// function testcase() {
//         try {
//             var obj = {};
//             var data = "data";
//             Object.defineProperty(obj, "accProperty", {
//                 set: function (value) {
//                     "use strict";
//                     eval("var public = 1;");
//                     data = value;
//                 }
//             });

//             obj.accProperty = "overrideData";

//             return false;
//         } catch (e) {
//             return e instanceof SyntaxError && data === "data";
//         }
//     }
// runTestCase(testcase);
