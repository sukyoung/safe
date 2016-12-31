var data = '{"foo": 123}';
var obj = JSON.parse(data);

var __result1;
var __expect1 = "TypeError";
try {
  obj.foo();
} catch(e) {
  __result1 = e.name;
}
