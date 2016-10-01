//   TODO [[DefineOwnProperty]] for Array object
//   function testcase() 
//   {
//     var arr = [];
//     var toStringAccessed = false;
//     var valueOfAccessed = false;
//     var proto = {
//       value : {
//         valueOf : (function () 
//         {
//           valueOfAccessed = true;
//           return 2;
//         })
//       }
//     };
//     var Con = (function () 
//     {
//       
//     });
//     Con.prototype = proto;
//     var child = new Con();
//     Object.defineProperty(child, "value", {
//       value : {
//         toString : (function () 
//         {
//           toStringAccessed = true;
//           return 3;
//         })
//       }
//     });
//     Object.defineProperties(arr, {
//       length : child
//     });
//     return arr.length === 3 && toStringAccessed && ! valueOfAccessed;
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
