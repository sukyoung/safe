//  TODO [[DefaultValue]]
//   var __obj = {
//     valueOf : (function () 
//     {
//       return {
        
//       };
//     }),
//     toString : (function () 
//     {
//       return 1;
//     })
//   };
//   var __obj2 = {
//     toString : (function () 
//     {
//       throw "inend";
//     })
//   };
//   try
// {    var x = "ABB\u0041BABAB\u0031BBAA".substring(__obj, __obj2);
//     $FAIL('#1: var x = "ABB\\u0041BABAB\\u0031BBAA".substring(__obj,__obj2) lead to throw exception');}
//   catch (e)
// {    {
//       var __result1 = e !== "inend";
//       var __expect1 = false;
//     }}
