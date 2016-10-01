//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var proto = {
//       get : (function () 
//       {
//         return "inheritedDataProperty";
//       })
//     };
//     var ConstructFun = (function () 
//     {
//       
//     });
//     ConstructFun.prototype = proto;
//     var child = new ConstructFun();
//     Object.defineProperty(child, "get", {
//       get : (function () 
//       {
//         return (function () 
//         {
//           return "ownAccessorProperty";
//         });
//       })
//     });
//     Object.defineProperty(obj, "property", child);
//     return obj.property === "ownAccessorProperty";
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
