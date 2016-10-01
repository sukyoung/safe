//   TODO dataPropertyAttributesAreCorrect
//   function testcase() 
//   {
//     var proto = {
//       
//     };
//     Object.defineProperty(proto, "prop", {
//       value : 11,
//       configurable : false
//     });
//     var Con = (function () 
//     {
//       
//     });
//     Con.prototype = proto;
//     var obj = new Con();
//     Object.defineProperties(obj, {
//       prop : {
//         value : 12,
//         configurable : true
//       }
//     });
//     return dataPropertyAttributesAreCorrect(obj, "prop", 12, false, false, true);
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
