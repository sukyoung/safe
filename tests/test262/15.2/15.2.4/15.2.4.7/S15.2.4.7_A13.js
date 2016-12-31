var __result1;
try {
  Object.prototype.propertyIsEnumerable.call(null, 'foo');
} catch (e) {
  __result1 = e instanceof @TypeErr;
}
var __expect1 = true;
