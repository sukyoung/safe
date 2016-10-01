//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var proto = {
//       
//     };
//     var accessed = false;
//     Object.defineProperty(proto, "enumerable", {
//       get : (function () 
//       {
//         return false;
//       })
//     });
//     var Con = (function () 
//     {
//       
//     });
//     Con.prototype = proto;
//     var descObj = new Con();
//     Object.defineProperty(descObj, "enumerable", {
//       set : (function () 
//       {
//         
//       })
//     });
//     Object.defineProperties(obj, {
//       prop : descObj
//     });
//     for(var property in obj)
//     {
//       if (property === "prop")
//       {
//         accessed = true;
//       }
//     }
//     return ! accessed;
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
