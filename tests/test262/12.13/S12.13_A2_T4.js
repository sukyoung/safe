  try
{    throw "exception #1";}
  catch (e)
{    if (e !== "exception #1")
      $ERROR('#1: Exception ==="exception #1". Actual:  Exception ===' + e);}

  var b = "exception #1";
  try
{    throw b;}
  catch (e)
{    if (e !== "exception #1")
      $ERROR('#2: Exception ==="exception #1". Actual:  Exception ===' + e);}

  