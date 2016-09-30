  try
{    new Array(NaN);
    $ERROR('#1.1: new Array(NaN) throw RangeError. Actual: ' + (new Array(NaN)));}
  catch (e)
{    {
      var __result1 = (e instanceof RangeError) !== true;
      var __expect1 = false;
    }}

  try
{    new Array(Number.POSITIVE_INFINITY);
    $ERROR('#2.1: new Array(Number.POSITIVE_INFINITY) throw RangeError. Actual: ' + (new Array(Number.POSITIVE_INFINITY)));}
  catch (e)
{    {
      var __result2 = (e instanceof RangeError) !== true;
      var __expect2 = false;
    }}

  try
{    new Array(Number.NEGATIVE_INFINITY);
    $ERROR('#3.1: new Array(Number.NEGATIVE_INFINITY) throw RangeError. Actual: ' + (new Array(Number.NEGATIVE_INFINITY)));}
  catch (e)
{    {
      var __result3 = (e instanceof RangeError) !== true;
      var __expect3 = false;
    }}

  