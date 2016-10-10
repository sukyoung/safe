  try
{    var __val = __func();
}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  var __func = (function __func() 
  {
    return "ONE";
  });
  var __val = __func();
  {
    var __result2 = __val !== "ONE";
    var __expect2 = false;
  }
  __func = (function __func() 
  {
    return "TWO";
  });
  var __val = __func();
  {
    var __result3 = __val !== "TWO";
    var __expect3 = false;
  }
  
