//  TODO [[DefaultValue]]
//   var __obj = {
//     toString : (function () 
//     {
//       return {
        
//       };
//     }),
//     valueOf : (function () 
//     {
//       throw "intostr";
//     })
//   };
//   __obj.toLowerCase = String.prototype.toLowerCase;
//   try
// {    var x = __obj.toLowerCase();
//     $FAIL('#1: "var x = __obj.toLowerCase()" lead to throwing exception');}
//   catch (e)
// {    {
//       var __result1 = e !== "intostr";
//       var __expect1 = false;
//     }}
