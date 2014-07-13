function recursive(d) {
  var other;
  if (d) {
    other = "FOO";
    recursive(!d);
//    dumpValue(other);
    __result1 = other;  // for SAFE
  }
}
var __expect1 = "FOO";  // for SAFE

recursive(true);
