  try
{    __func = __func;
    $ERROR('#1: "__func=__func" lead to throwing exception');}
  catch (e)
{    ;}

  try
{    if ((function __func() 
    {
      throw "FunctionExpression";
    }))
      (function () 
      {
        throw "TrueBranch";
      })();
    else
      (function () 
      {
        "MissBranch";
      })();}
  catch (e)
{    {
      var __result1 = e !== "TrueBranch";
      var __expect1 = false;
    }}

  try
{    __func = __func;
    $ERROR('#3: "__func=__func" lead to throwing exception');}
  catch (e)
{    ;}

  