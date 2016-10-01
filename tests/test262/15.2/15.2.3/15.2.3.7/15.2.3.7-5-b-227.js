//   TODO getter/setter
//   function testcase() 
//   {
//     var data = "data";
//     var obj = {
//       
//     };
//     try
// {      Object.defineProperties(obj, {
//         descObj : {
//           get : (function () 
//           {
//             return data;
//           })
//         }
//       });
//       obj.descObj = "overrideData";
//       var desc = Object.getOwnPropertyDescriptor(obj, "descObj");
//       return obj.hasOwnProperty("descObj") && typeof (desc.set) === "undefined" && data === "data";}
//     catch (e)
// {      return false;}
// 
//   }
//   {
//     var __result1 = testcase();
//     var __expect1 = true;
//   }
//   
