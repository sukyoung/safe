var __result1 = isNaN.propertyIsEnumerable('length');
var __expect1 = false;

var result = true;
for (p in isNaN){
  if (p === "length") {
    result = false;
  }
}
var __result2 = result;
var __expect2 = true;
