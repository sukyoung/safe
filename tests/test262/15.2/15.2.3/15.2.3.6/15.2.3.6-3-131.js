//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var proto = {
//       
//     };
//     Object.defineProperty(proto, "value", {
//       get : (function () 
//       {
//         return "inheritedAccessorProperty";
//       })
//     });
//     var ConstructFun = (function () 
//     {
//       
//     });
//     ConstructFun.prototype = proto;
//     var child = new ConstructFun();
//     Object.defineProperty(child, "value", {
//       value : "ownDataProperty"
//     });
//     Object.defineProperty(obj, "property", child);
//     return obj.property === "ownDataProperty";
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
