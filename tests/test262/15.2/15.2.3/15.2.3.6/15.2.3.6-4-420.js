//   TODO getter/setter
//   function testcase() 
//   {
//     var foo = (function () 
//     {
//       
//     });
//     try
// {      Object.defineProperty(Function.prototype, "prop", {
//         value : 1001,
//         writable : false,
//         enumerable : false,
//         configurable : true
//       });
//       var obj = foo.bind({
//         
//       });
//       obj.prop = 1002;
//       return ! obj.hasOwnProperty("prop") && obj.prop === 1001;}
//     finally
// {      delete Function.prototype.prop;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
