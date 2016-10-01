//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var proto = {
//       
//     };
//     Object.defineProperty(proto, "writable", {
//       get : (function () 
//       {
//         return true;
//       })
//     });
//     var Con = (function () 
//     {
//       
//     });
//     Con.prototype = proto;
//     var descObj = new Con();
//     Object.defineProperties(obj, {
//       property : descObj
//     });
//     obj.property = "isWritable";
//     return obj.hasOwnProperty("property") && obj.property === "isWritable";
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
