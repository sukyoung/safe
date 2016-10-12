// TODO unicode
//   try
// {    var xx = 1;
//     {
//       var __result1 = xx !== 1;
//       var __expect1 = false;
//     }}
//   catch (e)
// {    $ERROR('#1.2: var x\\u0078 = 1; xx === 1. Actual: ' + (xx));}

//   try
// {    var xx = 2;
//     {
//       var __result2 = xx !== 2;
//       var __expect2 = false;
//     }}
//   catch (e)
// {    $ERROR('#2.2: var \\u0078\\u0078 = 1; xx === 2. Actual: ' + (xx));}

//   try
// {    var \u0024\u0024 = 3;
//     {
//       var __result3 = $$ !== 3;
//       var __expect3 = false;
//     }}
//   catch (e)
// {    $ERROR('#3.2: var \\u0024\\u0024 = 1; $$ === 3. Actual: ' + ($$));}

//   try
// {    var \u005F\u005F = 4;
//     {
//       var __result4 = __ !== 4;
//       var __expect4 = false;
//     }}
//   catch (e)
// {    $ERROR('#4.2: var \\u005F\\u005F = 1; __ === 4. Actual: ' + (__));}

