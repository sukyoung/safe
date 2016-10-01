//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var proto = {
//       value : "inheritedDataProperty"
//     };
//     var Con = (function () 
//     {
//       
//     });
//     Con.prototype = proto;
//     var descObj = new Con();
//     Object.defineProperty(descObj, "value", {
//       get : (function () 
//       {
//         return "ownAccessorProperty";
//       })
//     });
//     Object.defineProperties(obj, {
//       property : descObj
//     });
//     return obj.property === "ownAccessorProperty";
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
