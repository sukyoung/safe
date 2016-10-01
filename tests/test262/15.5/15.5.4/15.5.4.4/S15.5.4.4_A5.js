//  TODO [[DefaultValue]]
//   var __obj = {
//     valueOf : 1,
//     toString : (function () 
//     {
//       throw 'intostring';
//     }),
//     charAt : String.prototype.charAt
//   };
//   try
// {    var x = __obj.charAt();
// }
//   catch (e)
// {
//       var __result1 = e !== 'intostring';
//       var __expect1 = false;
// }
