// TODO eval: statement
//   try
// {    var identifier = "x" + "x";
//     eval("var " + identifier + "=1");
//     {
//       var __result1 = xx !== 1;
//       var __expect1 = false;
//     }}
//   catch (e)
// {    $ERROR('#1.2: var identifier = "x" + "x"; eval("var " + identifier + "=1"); xx === 1. Actual: ' + (xx));}

//   try
// {    var identifier = "x" + String.fromCharCode(0x78);
//     eval("var " + identifier + "=2");
//     {
//       var __result2 = xx !== 2;
//       var __expect2 = false;
//     }}
//   catch (e)
// {    $ERROR('#2.2: var identifier = "x" + String.fromCharCode(0x0078); eval("var " + identifier + "=2"); xx === 2. Actual: ' + (xx));}

//   try
// {    var identifier = String.fromCharCode(0x78) + String.fromCharCode(0x78);
//     eval("var " + identifier + "=3");
//     {
//       var __result3 = xx !== 3;
//       var __expect3 = false;
//     }}
//   catch (e)
// {    $ERROR('#3.2: var identifier = String.fromCharCode(0x0078) + String.fromCharCode(0x0078); eval("var " + identifier + "=3"); xx === 3. Actual: ' + (xx));}

//   try
// {    var identifier = "$" + String.fromCharCode(0x78);
//     eval("var " + identifier + "=4");
//     {
//       var __result4 = $x !== 4;
//       var __expect4 = false;
//     }}
//   catch (e)
// {    $ERROR('#4.2: var identifier = "$" + String.fromCharCode(0x0078); eval("var " + identifier + "=4"); $x === 4. Actual: ' + ($x));}

//   try
// {    var identifier = "_" + String.fromCharCode(0x78);
//     eval("var " + identifier + "=5");
//     {
//       var __result5 = _x !== 5;
//       var __expect5 = false;
//     }}
//   catch (e)
// {    $ERROR('#5.2: var identifier = "_" + String.fromCharCode(0x0078); eval("var " + identifier + "=5"); _x === 5. Actual: ' + (_x));}

//   try
// {    var xx = 6;
//     {
//       var __result6 = xx !== 6;
//       var __expect6 = false;
//     }}
//   catch (e)
// {    $ERROR('#6.2: var \\u0078x = 1; xx === 6. Actual: ' + (xx));}

