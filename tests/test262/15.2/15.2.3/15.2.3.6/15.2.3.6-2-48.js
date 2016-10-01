//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var toStringAccessed = false;
//     var valueOfAccessed = false;
//     var proto = {
//       toString : (function () 
//       {
//         toStringAccessed = true;
//         return "test";
//       })
//     };
//     var ConstructFun = (function () 
//     {
//       
//     });
//     ConstructFun.prototype = proto;
//     var child = new ConstructFun();
//     child.valueOf = (function () 
//     {
//       valueOfAccessed = true;
//       return "10";
//     });
//     Object.defineProperty(obj, child, {
//       
//     });
//     return obj.hasOwnProperty("test") && ! valueOfAccessed && toStringAccessed;
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
