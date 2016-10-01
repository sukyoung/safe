//  TODO [[DefaultValue]]
//   var __obj = {
//     valueOf : String.prototype.valueOf
//   };
//   {
//     var __result1 = typeof __obj["valueOf"] !== "function";
//     var __expect1 = false;
//   }
//   try
// {    var x = (__obj == 1);
//     $FAIL('#2: "var __obj={valueOf : String.prototype.valueOf}; var x = (__obj == 1)" lead to throwing exception');}
//   catch (e)
// {    {
//       var __result2 = ! (e instanceof TypeError);
//       var __expect2 = false;
//     }}

  
