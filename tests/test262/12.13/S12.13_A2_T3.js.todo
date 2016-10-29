  try
{    throw true;}
  catch (e)
{    if (e !== true)
      $ERROR('#1: Exception ===true. Actual:  Exception ===' + e);}

  try
{    throw false;}
  catch (e)
{    if (e !== false)
      $ERROR('#2: Exception ===false. Actual:  Exception ===' + e);}

  var b = false;
  try
{    throw b;}
  catch (e)
{    if (e !== false)
      $ERROR('#3: Exception ===false. Actual:  Exception ===' + e);}

  var b = true;
  try
{    throw b;}
  catch (e)
{    if (e !== true)
      $ERROR('#4: Exception ===true. Actual:  Exception ===' + e);}

  