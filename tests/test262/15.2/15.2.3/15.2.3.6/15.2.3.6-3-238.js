//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var data = "data";
//     var proto = {
//       set : (function (value) 
//       {
//         data = value;
//       })
//     };
//     var ConstructFun = (function () 
//     {
//       
//     });
//     ConstructFun.prototype = proto;
//     var child = new ConstructFun();
//     Object.defineProperty(obj, "property", child);
//     obj.property = "overrideData";
//     return obj.hasOwnProperty("property") && data === "overrideData";
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
