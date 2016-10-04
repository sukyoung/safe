  try
{    for (__key in null)
    {
      var key = __key;
    }
    ;}
  catch (e)
{    $ERROR('#1: "for(__key in null){}" does not lead to throwing exception');}

  {
    var __result1 = key !== undefined;
    var __expect1 = false;
  }
  