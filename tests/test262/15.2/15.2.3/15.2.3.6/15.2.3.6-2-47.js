//   TODO getter/setter
//   function testcase() 
//   {
//     var obj = {
//       
//     };
//     var toStringAccessed = false;
//     var valueOfAccessed = false;
//     var ownProp = {
//       toString : (function () 
//       {
//         toStringAccessed = true;
//         return {
//           
//         };
//       }),
//       valueOf : (function () 
//       {
//         valueOfAccessed = true;
//         return {
//           
//         };
//       })
//     };
//     try
// {      Object.defineProperty(obj, ownProp, {
//         
//       });
//       return false;}
//     catch (e)
// {      return valueOfAccessed && toStringAccessed && e instanceof TypeError;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
