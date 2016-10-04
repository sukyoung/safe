  try
{    for ((function () 
    {
      throw "NoInExpression";
    })();;)
    {
      throw "Statement";
    }
    $ERROR('#1: (function(){throw "NoInExpression"})() lead to throwing exception');}
  catch (e)
{    {
      var __result1 = e !== "NoInExpression";
      var __expect1 = false;
    }}

  