// TODO eval: string escape
// \u00A0var\u00A0x\u00A0 = \u00A01\u00A0;
// {
//   var __result1 = x !== 1;
//   var __expect1 = false;
// }
// eval("\u00A0" + "var" + "\u00A0" + "x" + "\u00A0" + "=" + "\u00A0" + "1" + "\u00A0");
// {
//   var __result2 = x !== 1;
//   var __expect2 = false;
// }

