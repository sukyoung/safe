  try
{    for (__key in undefined)
    {
      var key = __key;
    }
    ;}
  catch (e)
{    $ERROR('#1: "for(key in undefined){}" does not lead to throwing exception');}

  {
    var __result1 = key !== undefined;
    var __expect1 = false;
  }
  