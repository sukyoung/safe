function empty() {}

function recursive(disks) {
  var other;
  if (disks == 1) {
    empty();
  } else {
    other = "FOO";
    recursive(disks - 1);
//    dumpValue(other);
    __result1 = other;  // for SAFE
  }
}
var __expect1 = "FOO";  // for SAFE

recursive(13);
