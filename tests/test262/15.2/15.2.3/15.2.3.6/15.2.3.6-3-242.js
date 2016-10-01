//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var proto = {
//       
//     };
//     var data = "data";
//     Object.defineProperty(proto, "set", {
//       get : (function () 
//       {
//         return (function (value) 
//         {
//           data = value;
//         });
//       })
//     });
//     var ConstructFun = (function () 
//     {
//       
//     });
//     ConstructFun.prototype = proto;
//     var child = new ConstructFun();
//     Object.defineProperty(obj, "property", child);
//     obj.property = "inheritedAccessorProperty";
//     return obj.hasOwnProperty("property") && data === "inheritedAccessorProperty";
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
