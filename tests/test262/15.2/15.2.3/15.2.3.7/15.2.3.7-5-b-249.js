//   TODO getter/setter
//   function testcase() 
//   {
//     var data = "data";
//     var fun = (function () 
//     {
//       return arguments;
//     });
//     var arg = fun();
//     var setFun = (function (value) 
//     {
//       data = value;
//     });
//     arg.prop = {
//       set : setFun
//     };
//     var obj = {
//       
//     };
//     Object.defineProperties(obj, arg);
//     obj.prop = "argData";
//     return obj.hasOwnProperty("prop") && data === "argData";
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
