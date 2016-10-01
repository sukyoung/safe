//   TODO getter/setter
//   function testcase() 
//   {
//     var data = "data";
//     try
// {      Object.defineProperty(Array.prototype, "prop", {
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
//       var arrObj = [];
//       arrObj.prop = "myOwnProperty";
//       return ! arrObj.hasOwnProperty("prop") && arrObj.prop === "myOwnProperty" && data === "myOwnProperty";}
//     finally
// {      delete Array.prototype.prop;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
