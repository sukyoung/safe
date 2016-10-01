//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     try
// {      __Global.get = (function () 
//       {
//         return "globalGetProperty";
//       });
//       Object.defineProperty(obj, "property", __Global);
//       return obj.property === "globalGetProperty";}
//     finally
// {      delete __Global.get;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
