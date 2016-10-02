/*
var __result1 = true;
  try
{   var __val = __func();}
  catch (e)
{   __result1 = false; }
var __expect1 = true;
*/
var __val = __func();

  {
    var __result2 = __val !== "SECOND";
    var __expect2 = false;
  }
  function __func() 
  {
    return "FIRST";
  }
  ;
  __val = __func();
/*
  {
    var __result3 = __val !== "SECOND";
    var __expect3 = false;
  }
*/
  function __func() 
  {
    return "SECOND";
  }
  ;
