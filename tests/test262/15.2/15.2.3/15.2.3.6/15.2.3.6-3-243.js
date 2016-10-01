//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var data1 = "data";
//     var data2 = "data";
//     var proto = {
//       set : (function (value) 
//       {
//         data1 = value;
//       })
//     };
//     var ConstructFun = (function () 
//     {
//       
//     });
//     ConstructFun.prototype = proto;
//     var child = new ConstructFun();
//     Object.defineProperty(child, "set", {
//       get : (function () 
//       {
//         return (function (value) 
//         {
//           data2 = value;
//         });
//       })
//     });
//     Object.defineProperty(obj, "property", child);
//     obj.property = "ownAccessorProperty";
//     return obj.hasOwnProperty("property") && data1 === "data" && data2 === "ownAccessorProperty";
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
