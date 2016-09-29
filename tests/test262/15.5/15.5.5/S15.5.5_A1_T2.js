  try
{    String("a|b")();
    $FAIL('#1: String("a|b")() lead to throwing exception');}
  catch (e)
{    {
      var __result1 = ! (e instanceof TypeError);
      var __expect1 = false;
    }}

  