  try
{    var __res = __func();}
  catch (e)
{
}

  {
    var __result1 = __res !== "SECOND";
    var __expect1 = false;
  }
  function __func() 
  {
    return "FIRST";
  }
  ;
  __res = __func();
  {
    var __result2 = __res !== "SECOND";
    var __expect2 = false;
  }
  function __func() 
  {
    return "SECOND";
  }
  ;
  
