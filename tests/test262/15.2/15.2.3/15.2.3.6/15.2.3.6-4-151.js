//   TODO getter/setter
//   function testcase() 
//   {
//     var arrObj = [];
//     var toStringAccessed = false;
//     var valueOfAccessed = false;
//     var proto = {
//       valueOf : (function () 
//       {
//         valueOfAccessed = true;
//         return 2;
//       })
//     };
//     var ConstructFun = (function () 
//     {
//       
//     });
//     ConstructFun.prototype = proto;
//     var child = new ConstructFun();
//     child.toString = (function () 
//     {
//       toStringAccessed = true;
//       return 3;
//     });
//     Object.defineProperty(arrObj, "length", {
//       value : child
//     });
//     return arrObj.length === 2 && ! toStringAccessed && valueOfAccessed;
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
