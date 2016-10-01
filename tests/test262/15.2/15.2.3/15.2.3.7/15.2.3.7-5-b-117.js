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
//     var Con = (function () 
//     {
//       
//     });
//     Con.prototype = proto;
//     var descObj = new Con();
//     Object.defineProperty(descObj, "value", {
//       value : "ownDataProperty"
//     });
//     Object.defineProperties(obj, {
//       property : descObj
//     });
//     return obj.property === "ownDataProperty";
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
