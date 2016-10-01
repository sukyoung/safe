//   TODO getter/setter
//   function testcase() 
//   {
//     var proto = {
//       
//     };
//     Object.defineProperty(proto, "prop", {
//       set : (function () 
//       {
//         
//       }),
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
//         get : (function () 
//         {
//           return 12;
//         }),
//         configurable : true
//       }
//     });
//     return obj.hasOwnProperty("prop") && obj.prop === 12;
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
