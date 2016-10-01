  try
{    new new String;
    $FAIL('#1: "new new String" lead to throwing exception');}
  catch (e)
{    {
      var __result1 = ! (e instanceof TypeError);
      var __expect1 = false;
    }}

  