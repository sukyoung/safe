// TODO strict
// function testcase() {

//         try {
//             eval("var obj = {\
//                 get _11_1_5_6_2_2() {\
//                    \"use strict\";\
//                    public = 42;\
//                    return public;\
//                 }\
//             };\
//             var _11_1_5_6_2_2 = obj._11_1_5_6_2_2;");
//             return false;
//         } catch (e) {
//             return e instanceof SyntaxError;
//         }
//     }
// runTestCase(testcase);
