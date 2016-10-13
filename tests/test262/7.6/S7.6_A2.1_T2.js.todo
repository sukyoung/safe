// TODO eval: statement
//   try
// {    var identifier = "x" + "$";
//     eval("var " + identifier + "=1");
//     {
//       var __result1 = x$ !== 1;
//       var __expect1 = false;
//     }}
//   catch (e)
// {    $ERROR('#1.2: var identifier = "x" + "$"; eval("var " + identifier + "=1"); x$ === 1. Actual: ' + (x$));}

//   try
// {    var identifier = String.fromCharCode(0x78) + "$";
//     eval("var " + identifier + "=2");
//     {
//       var __result2 = x$ !== 2;
//       var __expect2 = false;
//     }}
//   catch (e)
// {    $ERROR('#2.2: var identifier = String.fromCharCode(0x0078) + "$"; eval("var " + identifier + "=2"); x$ === 2. Actual: ' + (x$));}

//   try
// {    var identifier = "$" + "$";
//     eval("var " + identifier + "=3");
//     {
//       var __result3 = $$ !== 3;
//       var __expect3 = false;
//     }}
//   catch (e)
// {    $ERROR('#3.2: var identifier = "$" + "$"; eval("var " + identifier + "=3"); $$ === 3. Actual: ' + ($$));}

//   try
// {    var identifier = String.fromCharCode(0x24) + String.fromCharCode(0x24);
//     eval("var " + identifier + "=4");
//     {
//       var __result4 = $$ !== 4;
//       var __expect4 = false;
//     }}
//   catch (e)
// {    $ERROR('#4.2: var identifier = String.fromCharCode(0x0024) + String.fromCharCode(0x0024); eval("var " + identifier + "=4"); $$ === 4. Actual: ' + ($$));}

//   try
// {    var identifier = "_" + "$";
//     eval("var " + identifier + "=5");
//     {
//       var __result5 = _$ !== 5;
//       var __expect5 = false;
//     }}
//   catch (e)
// {    $ERROR('#5.2: var identifier = "_" + "$"; eval("var " + identifier + "=5"); _$ === 5. Actual: ' + (_$));}

//   try
// {    var x$ = 6;
//     {
//       var __result6 = x$ !== 6;
//       var __expect6 = false;
//     }}
//   catch (e)
// {    $ERROR('#6.2: var \\u0078$ = 1; x$ === 6. Actual: ' + (x$));}

