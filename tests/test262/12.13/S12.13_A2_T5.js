  try
{    throw 13;}
  catch (e)
{    if (e !== 13)
      $ERROR('#1: Exception ===13. Actual:  Exception ===' + e);}

  var b = 13;
  try
{    throw b;}
  catch (e)
{    if (e !== 13)
      $ERROR('#2: Exception ===13. Actual:  Exception ===' + e);}

  try
{    throw 2.13;}
  catch (e)
{    if (e !== 2.13)
      $ERROR('#3: Exception ===2.13. Actual:  Exception ===' + e);}

  try
{    throw NaN;}
  catch (e)
{    if (! isNaN(e))
      $ERROR('#4: Exception is NaN');}

  try
{    throw + Infinity;}
  catch (e)
{    if (e !== + Infinity)
      $ERROR('#5: Exception ===+Infinity. Actual:  Exception ===' + e);}

  try
{    throw - Infinity;}
  catch (e)
{    if (e !== - Infinity)
      $ERROR('#6: Exception ===-Infinity. Actual:  Exception ===' + e);}

  try
{    throw + 0;}
  catch (e)
{    if (e !== + 0)
      $ERROR('#7: Exception ===+0. Actual:  Exception ===' + e);}

  try
{    throw - 0;}
  catch (e)
{    if (e !== - 0)
      $ERROR('#8: Exception ===-0. Actual:  Exception ===' + e);}

  