//   TODO getter/setter
//   function testcase() 
//   {
//     var proto = {
//       
//     };
//     Object.defineProperty(proto, "prop", {
//       value : 11,
//       configurable : true
//     });
//     var Con = (function () 
//     {
//       
//     });
//     Con.prototype = proto;
//     var obj = new Con();
//     Object.defineProperty(obj, "prop", {
//       get : (function () 
//       {
//         return 12;
//       }),
//       configurable : false
//     });
//     try
// {      Object.defineProperties(obj, {
//         prop : {
//           value : 13,
//           configurable : true
//         }
//       });
//       return false;}
//     catch (e)
// {      return (e instanceof TypeError) && obj.prop === 12;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
