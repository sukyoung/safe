//   TODO getter/setter
//   function testcase() 
//   {
//     var data = "data";
//     try
// {      Object.defineProperty(Boolean.prototype, "prop", {
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
//       var boolObj = new Boolean();
//       var verifyEnumerable = false;
//       for(var p in boolObj)
//       {
//         if (p === "prop")
//         {
//           verifyEnumerable = true;
//         }
//       }
//       return ! boolObj.hasOwnProperty("prop") && verifyEnumerable;}
//     finally
// {      delete Boolean.prototype.prop;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
