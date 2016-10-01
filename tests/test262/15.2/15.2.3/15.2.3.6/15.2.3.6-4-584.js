//   TODO getter/setter
//   function testcase() 
//   {
//     var data = "data";
//     try
// {      Object.defineProperty(Date.prototype, "prop", {
//         get : (function () 
//         {
//           return data;
//         }),
//         set : (function (value) 
//         {
//           data = value;
//         }),
//         enumerable : true,
//         configurable : true
//       });
//       var dateObj = new Date();
//       dateObj.prop = "myOwnProperty";
//       return ! dateObj.hasOwnProperty("prop") && dateObj.prop === "myOwnProperty" && data === "myOwnProperty";}
//     finally
// {      delete Date.prototype.prop;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
