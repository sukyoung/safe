//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var data = "data";
//     try
// {      RegExp.prototype.set = (function (value) 
//       {
//         data = value;
//       });
//       var regObj = new RegExp();
//       Object.defineProperty(obj, "property", regObj);
//       obj.property = "overrideData";
//       return obj.hasOwnProperty("property") && data === "overrideData";}
//     finally
// {      delete RegExp.prototype.set;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
