//   TODO getter/setter
//   function testcase() 
//   {
//     var foo = (function () 
//     {
//       
//     });
//     var data = "data";
//     try
// {      Object.defineProperty(Function.prototype, "prop", {
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
//       var obj = foo.bind({
//         
//       });
//       var verifyEnumerable = false;
//       for(var p in obj)
//       {
//         if (p === "prop")
//         {
//           verifyEnumerable = true;
//         }
//       }
//       return ! obj.hasOwnProperty("prop") && verifyEnumerable;}
//     finally
// {      delete Function.prototype.prop;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
