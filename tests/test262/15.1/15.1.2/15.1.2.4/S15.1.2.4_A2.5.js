var __result1 = this.propertyIsEnumerable('isNaN');
var __expect1 = false;

var result = true;
for (p in this){
  if (p === "isNaN") {
    result = false;
  }
}
var __result2 = result;
var __expect2 = true;
