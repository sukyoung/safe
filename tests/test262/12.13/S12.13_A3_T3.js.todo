  try
{    throw 10 + 3;}
  catch (e)
{    if (e !== 13)
      $ERROR('#1: Exception ===13(operaton +). Actual:  Exception ===' + e);}

  var b = 10;
  var a = 3;
  try
{    throw a + b;}
  catch (e)
{    if (e !== 13)
      $ERROR('#2: Exception ===13(operaton +). Actual:  Exception ===' + e);}

  try
{    throw 3.15 - 1.02;}
  catch (e)
{    if (e !== 2.13)
      $ERROR('#3: Exception ===2.13(operaton -). Actual:  Exception ===' + e);}

  try
{    throw 2 * 2;}
  catch (e)
{    if (e !== 4)
      $ERROR('#4: Exception ===4(operaton *). Actual:  Exception ===' + e);}

  try
{    throw 1 + Infinity;}
  catch (e)
{    if (e !== + Infinity)
      $ERROR('#5: Exception ===+Infinity(operaton +). Actual:  Exception ===' + e);}

  try
{    throw 1 - Infinity;}
  catch (e)
{    if (e !== - Infinity)
      $ERROR('#6: Exception ===-Infinity(operaton -). Actual:  Exception ===' + e);}

  try
{    throw 10 / 5;}
  catch (e)
{    if (e !== 2)
      $ERROR('#7: Exception ===2(operaton /). Actual:  Exception ===' + e);}

  try
{    throw 8 >> 2;}
  catch (e)
{    if (e !== 2)
      $ERROR('#8: Exception ===2(operaton >>). Actual:  Exception ===' + e);}

  try
{    throw 2 << 2;}
  catch (e)
{    if (e !== 8)
      $ERROR('#9: Exception ===8(operaton <<). Actual:  Exception ===' + e);}

  try
{    throw 123 % 100;}
  catch (e)
{    if (e !== 23)
      $ERROR('#10: Exception ===23(operaton %). Actual:  Exception ===' + e);}

  