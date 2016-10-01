//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var data = "data";
//     var attributes = {
//       
//     };
//     Object.defineProperty(attributes, "set", {
//       get : (function () 
//       {
//         return (function (value) 
//         {
//           data = value;
//         });
//       })
//     });
//     Object.defineProperty(obj, "property", attributes);
//     obj.property = "ownAccessorProperty";
//     return obj.hasOwnProperty("property") && data === "ownAccessorProperty";
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
