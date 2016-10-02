// Syntax error
//   try
// {    eval("function __func(){/ ABC}");
//     $ERROR('#1: eval("function __func(){/ ABC}") lead to throwing exception');}
//   catch (e)
// {    {
//       var __result1 = ! (e instanceof SyntaxError);
//       var __expect1 = false;
//     }}

//   try
// {    eval("function __func(){&1}");
//     $ERROR('#3: eval("function __func(){&1}") lead to throwing exception');}
//   catch (e)
// {    {
//       var __result2 = ! (e instanceof SyntaxError);
//       var __expect2 = false;
//     }}

//   try
// {    eval("function __func(){# ABC}");
//     $ERROR('#4: eval("function __func(){# ABC}") lead to throwing exception');}
//   catch (e)
// {    {
//       var __result3 = ! (e instanceof SyntaxError);
//       var __expect3 = false;
//     }}
