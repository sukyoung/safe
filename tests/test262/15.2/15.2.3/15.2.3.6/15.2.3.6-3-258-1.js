//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var data = "data";
//     try
// {      Object.prototype.set = (function (value) 
//       {
//         data = value;
//       });
//       var argObj = (function () 
//       {
//         return arguments;
//       })();
//       Object.defineProperty(obj, "property", argObj);
//       obj.property = "overrideData";
//       return obj.hasOwnProperty("property") && data === "overrideData";}
//     finally
// {      delete Object.prototype.set;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
