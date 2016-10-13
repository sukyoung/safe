  var b = true;
  try
{    throw b && false;}
  catch (e)
{    if (e !== false)
      $ERROR('#1: Exception === false(operaton &&). Actual:  Exception ===' + e);}

  var b = true;
  try
{    throw b || false;}
  catch (e)
{    if (e !== true)
      $ERROR('#2: Exception === true(operaton ||). Actual:  Exception ===' + e);}

  try
{    throw ! false;}
  catch (e)
{    if (e !== true)
      $ERROR('#3: Exception === true(operaton !). Actual:  Exception ===' + e);}

  var b = true;
  try
{    throw ! (b && false);}
  catch (e)
{    if (e !== true)
      $ERROR('#4: Exception === true(operaton &&). Actual:  Exception ===' + e);}

  