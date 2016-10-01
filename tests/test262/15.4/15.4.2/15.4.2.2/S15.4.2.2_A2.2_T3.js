  try
{    new Array(1.5);
    $ERROR('#1.1: new Array(1.5) throw RangeError. Actual: ' + (new Array(1.5)));}
  catch (e)
{    {
      var __result1 = (e instanceof RangeError) !== true;
      var __expect1 = false;
    }}

  try
{    new Array(Number.MAX_VALUE);
    $ERROR('#2.1: new Array(Number.MAX_VALUE) throw RangeError. Actual: ' + (new Array(Number.MAX_VALUE)));}
  catch (e)
{    {
      var __result2 = (e instanceof RangeError) !== true;
      var __expect2 = false;
    }}

  try
{    new Array(Number.MIN_VALUE);
    $ERROR('#3.1: new Array(Number.MIN_VALUE) throw RangeError. Actual: ' + (new Array(Number.MIN_VALUE)));}
  catch (e)
{    {
      var __result3 = (e instanceof RangeError) !== true;
      var __expect3 = false;
    }}

  