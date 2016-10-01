//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var getter = (function () 
//     {
//       return "inheritedDataProperty";
//     });
//     var proto = {
//       get : getter
//     };
//     var Con = (function () 
//     {
//       
//     });
//     Con.prototype = proto;
//     var descObj = new Con();
//     Object.defineProperties(obj, {
//       property : descObj
//     });
//     return obj.property === "inheritedDataProperty";
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
