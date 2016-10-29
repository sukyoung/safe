  try
{    throw 13;}
  catch (e)
{    if (e !== 13)
      $ERROR('#1: Exception ===13. Actual:  Exception ===' + e);}

  try
{    throw 10 + 3;}
  catch (e)
{    if (e !== 13)
      $ERROR('#2: Exception ===13. Actual:  Exception ===' + e);}

  var b = 13;
  try
{    throw b;}
  catch (e)
{    if (e !== 13)
      $ERROR('#3: Exception ===13. Actual:  Exception ===' + e);}

  var a = 3;
  var b = 10;
  try
{    throw a + b;}
  catch (e)
{    if (e !== 13)
      $ERROR('#4: Exception ===13. Actual:  Exception ===' + e);}

  try
{    throw 2.13;}
  catch (e)
{    if (e !== 2.13)
      $ERROR('#5: Exception ===2.13. Actual:  Exception ===' + e);}

  var ex = 2 / 3;
  try
{    throw 2 / 3;}
  catch (e)
{    if (e !== ex)
      $ERROR('#6: Exception ===2/3. Actual:  Exception ===' + e);}

  try
{    throw NaN;}
  catch (e)
{    if (! isNaN(e))
      $ERROR('#7: Exception is NaN');}

  try
{    throw + Infinity;}
  catch (e)
{    if (e !== + Infinity)
      $ERROR('#8: Exception ===+Infinity. Actual:  Exception ===' + e);}

  try
{    throw - Infinity;}
  catch (e)
{    if (e !== - Infinity)
      $ERROR('#9: Exception ===-Infinity. Actual:  Exception ===' + e);}

  try
{    throw + 0;}
  catch (e)
{    if (e !== + 0)
      $ERROR('#10: Exception ===+0. Actual:  Exception ===' + e);}

  try
{    throw - 0;}
  catch (e)
{    if (e !== - 0)
      $ERROR('#11: Exception ===-0. Actual:  Exception ===' + e);}

  