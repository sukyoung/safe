function __mFunc()
{
  var __accum = "";
  for(var i = 0;i < arguments.length;++ i)
  {
    __accum += arguments[i];
  }
  ;
  return __accum;
}
;
{
  var __result1 = __mFunc("A", "B", "C", "D", "E", "F") !== "ABCDEF";
  var __expect1 = false;
}
