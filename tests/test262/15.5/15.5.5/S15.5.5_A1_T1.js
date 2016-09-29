  var __str = new String;
  try
{    __str();
    $FAIL('#1: __str = new String; __str() lead to throwing exception');}
  catch (e)
{    {
      var __result1 = ! (e instanceof TypeError);
      var __expect1 = false;
    }}

  