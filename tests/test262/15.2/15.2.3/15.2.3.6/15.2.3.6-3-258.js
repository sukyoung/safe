//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var data = "data";
//     var argObj = (function () 
//     {
//       return arguments;
//     })();
//     argObj.set = (function (value) 
//     {
//       data = value;
//     });
//     Object.defineProperty(obj, "property", argObj);
//     obj.property = "overrideData";
//     return obj.hasOwnProperty("property") && data === "overrideData";
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
