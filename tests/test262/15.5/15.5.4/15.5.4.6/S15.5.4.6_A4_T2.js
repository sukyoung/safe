//  TODO [[DefaultValue]]
//   var __instance = {
//     toString : (function () 
//     {
//       throw "intostring";
//     })
//   };
//   var __obj = {
//     toString : (function () 
//     {
//       throw "infirstarg";
//     })
//   };
//   __instance.concat = String.prototype.concat;
//   try
// {    String.prototype.concat.call(__instance, __obj, notexist);
//     $FAIL('#1: "String.prototype.concat.call(__instance,__obj, notexist)" lead to throwing exception');}
//   catch (e)
// {    {
//       var __result1 = e !== "intostring";
//       var __expect1 = false;
//     }}

//   var notexist;
  
