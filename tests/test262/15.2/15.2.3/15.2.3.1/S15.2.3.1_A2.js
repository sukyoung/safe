{
  var __result1 = Object.propertyIsEnumerable('prototype');
  var __expect1 = false;
}
var cout = 0;
for (p in Object)
{
  if (p === "prototype")
    cout++;
}
{
  var __result2 = cout !== 0;
  var __expect2 = false;
}
