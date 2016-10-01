//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     try
// {      Object.prototype.get = (function () 
//       {
//         return "argumentGetProperty";
//       });
//       var argObj = (function () 
//       {
//         return arguments;
//       })();
//       Object.defineProperty(obj, "property", argObj);
//       return obj.property === "argumentGetProperty";}
//     finally
// {      delete Object.prototype.get;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
