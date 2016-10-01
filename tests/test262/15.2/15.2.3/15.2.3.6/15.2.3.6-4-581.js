//   TODO getter/setter
//   function testcase() 
//   {
//     var data = "data";
//     try
// {      Object.defineProperty(Number.prototype, "prop", {
//         get : (function () 
//         {
//           return data;
//         }),
//         enumerable : false,
//         configurable : true
//       });
//       var numObj = new Number();
//       numObj.prop = "myOwnProperty";
//       return ! numObj.hasOwnProperty("prop") && numObj.prop === "data" && data === "data";}
//     finally
// {      delete Number.prototype.prop;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
