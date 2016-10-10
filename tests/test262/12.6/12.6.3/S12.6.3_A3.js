  try
{    for ((function () 
    {
      __in__NotInExpression__ = "checked";
      __in__NotInExpression__2 = "passed";
    })();(function () 
    {
      throw "FirstExpression";
    })();(function () 
    {
      throw "SecondExpression";
    })())
    {
      __in__for = "reached";
    }
}
  catch (e)
{    {
      var __result1 = e !== "FirstExpression";
      var __expect1 = false;
    }}

  {
    var __result2 = (__in__NotInExpression__ !== "checked") & (__in__NotInExpression__2 !== "passed");
    var __expect2 = 0;
  }

  {
    var __result3 = typeof __in__for !== "undefined";
    var __expect3 = false;
  }
  
