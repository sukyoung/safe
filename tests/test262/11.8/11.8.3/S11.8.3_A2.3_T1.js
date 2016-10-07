// TODO [[DefaultValue]]
//   var x = {
//     valueOf : (function () 
//     {
//       throw "x";
//     })
//   };
//   var y = {
//     valueOf : (function () 
//     {
//       throw "y";
//     })
//   };
//   try
// {    x <= y;
//     $ERROR('#1.1: Should have thrown');}
//   catch (e)
// {    if (e === "y")
//     {
//       $ERROR('#1.2: First expression should be evaluated first');
//     }
//     else
//     {
//       {
//         var __result1 = e !== "x";
//         var __expect1 = false;
//       }
//     }}

