  try
{    throw "exception #1";}
  catch (e)
{    if (e !== "exception #1")
      $ERROR('#1: Exception ==="exception #1". Actual:  Exception ===' + e);}

  try
{    throw "exception" + " #1";}
  catch (e)
{    if (e !== "exception #1")
      $ERROR('#2: Exception ==="exception #1". Actual:  Exception ===' + e);}

  var b = "exception #1";
  try
{    throw b;}
  catch (e)
{    if (e !== "exception #1")
      $ERROR('#3: Exception ==="exception #1". Actual:  Exception ===' + e);}

  var a = "exception";
  var b = " #1";
  try
{    throw a + b;}
  catch (e)
{    if (e !== "exception #1")
      $ERROR('#4: Exception ==="exception #1". Actual:  Exception ===' + e);}

  