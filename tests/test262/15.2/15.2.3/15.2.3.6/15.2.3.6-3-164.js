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
//       set : (function () 
//       {
//         
//       })
//     });
//     var ConstructFun = (function () 
//     {
//       
//     });
//     ConstructFun.prototype = proto;
//     var child = new ConstructFun();
//     Object.defineProperty(obj, "property", child);
//     var beforeWrite = obj.hasOwnProperty("property");
//     obj.property = "isWritable";
//     var afterWrite = (typeof (obj.property) === "undefined");
//     return beforeWrite === true && afterWrite === true;
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
