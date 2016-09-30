  try
{    var __res = __func();
}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  var __func = (function () 
  {
    return "ONE";
  });
  var __res = __func();
  {
    var __result2 = __res !== "ONE";
    var __expect2 = false;
  }
  __func = (function () 
  {
    return "TWO";
  });
  var __res = __func();
  {
    var __result3 = __res !== "TWO";
    var __expect3 = false;
  }
  
