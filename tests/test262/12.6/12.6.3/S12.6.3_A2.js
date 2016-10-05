  try
{    for ((function () 
    {
      throw "NoInExpression";
    })();(function () 
    {
      throw "FirstExpression";
    })();(function () 
    {
      throw "SecondExpression";
    })())
    {
      var in_for = "reached";
    }
    $ERROR('#1: (function(){throw "NoInExpression";})() lead to throwing exception');}
  catch (e)
{    {
      var __result1 = e !== "NoInExpression";
      var __expect1 = false;
    }}

  {
    var __result2 = in_for !== undefined;
    var __expect2 = false;
  }
  