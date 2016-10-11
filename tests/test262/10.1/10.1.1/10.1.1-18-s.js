// TODO strict
// function testcase() {
//         "use strict";
//         try {
//             var obj = {};
//             var data = "data";
//             Object.defineProperty(obj, "accProperty", {
//                 set: function (value) {
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
