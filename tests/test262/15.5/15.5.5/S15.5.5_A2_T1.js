  var __str = new Object("");
  try
{    new __str;
    $FAIL('#1: __str = new Object(""); "new __str" lead to throwing exception');}
  catch (e)
{    {
      var __result1 = ! (e instanceof TypeError);
      var __expect1 = false;
    }}

  