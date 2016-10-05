  try
{    for (;;(function () 
    {
      throw "SecondExpression";
    })())
    {
      var __in__for = "reached";
    }
    $ERROR('#1: (function(){throw "SecondExpression"}() lead to throwing exception');}
  catch (e)
{    {
      var __result1 = e !== "SecondExpression";
      var __expect1 = false;
    }}

  {
    var __result2 = __in__for !== "reached";
    var __expect2 = false;
  }
  