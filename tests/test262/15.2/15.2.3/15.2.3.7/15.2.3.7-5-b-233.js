//   TODO getter/setter
//   function testcase() 
//   {
//     var data = "data";
//     var setFun = (function (value) 
//     {
//       data = value;
//     });
//     var proto = {
//       
//     };
//     Object.defineProperty(proto, "set", {
//       get : (function () 
//       {
//         return setFun;
//       })
//     });
//     var Con = (function () 
//     {
//       
//     });
//     Con.prototype = proto;
//     var child = new Con();
//     var obj = {
//       
//     };
//     Object.defineProperties(obj, {
//       prop : child
//     });
//     obj.prop = "overrideData";
//     return obj.hasOwnProperty("prop") && data === "overrideData";
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
