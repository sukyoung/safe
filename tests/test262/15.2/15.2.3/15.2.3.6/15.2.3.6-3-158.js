//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var attr = {
//       
//     };
//     Object.defineProperty(attr, "writable", {
//       get : (function () 
//       {
//         return true;
//       })
//     });
//     Object.defineProperty(obj, "property", attr);
//     var beforeWrite = obj.hasOwnProperty("property");
//     obj.property = "isWritable";
//     var afterWrite = (obj.property === "isWritable");
//     return beforeWrite === true && afterWrite === true;
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
