//   TODO getter/setter
//   function testcase() 
//   {
//     var proto = {
//       
//     };
//     Object.defineProperty(proto, "prop", {
//       get : (function () 
//       {
//         return 11;
//       }),
//       set : (function () 
//       {
//         
//       }),
//       configurable : true
//     });
//     var Con = (function () 
//     {
//       
//     });
//     Con.prototype = proto;
//     var obj = new Con();
//     Object.defineProperty(obj, "prop", {
//       set : (function () 
//       {
//         
//       }),
//       configurable : false
//     });
//     try
// {      Object.defineProperties(obj, {
//         prop : {
//           value : 12,
//           configurable : true
//         }
//       });
//       return false;}
//     catch (e)
// {      return (e instanceof TypeError);}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
