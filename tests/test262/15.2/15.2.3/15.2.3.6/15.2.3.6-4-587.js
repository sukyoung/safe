//   TODO getter/setter
//   function testcase() 
//   {
//     var data = "data";
//     try
// {      Object.defineProperty(Object.prototype, "prop", {
//         get : (function () 
//         {
//           return data;
//         }),
//         enumerable : false,
//         configurable : true
//       });
//       var verifyEnumerable = false;
//       for(var p in Math)
//       {
//         if (p === "prop")
//         {
//           verifyEnumerable = true;
//         }
//       }
//       return ! Math.hasOwnProperty("prop") && ! verifyEnumerable;}
//     finally
// {      delete Object.prototype.prop;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
