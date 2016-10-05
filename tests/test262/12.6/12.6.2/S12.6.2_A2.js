  try
{    while ((function () 
    {
      throw 1;
    })())
      __in__while = "reached";
    $ERROR('#1: \'while ((function(){throw 1})()) __in__while = "reached"\' lead to throwing exception');}
  catch (e)
{    {
      var __result1 = e !== 1;
      var __expect1 = false;
    }}

  {
    var __result2 = typeof __in__while !== "undefined";
    var __expect2 = false;
  }
  