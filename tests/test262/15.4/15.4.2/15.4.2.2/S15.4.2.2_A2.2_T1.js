  try
{    new Array(- 1);
    $ERROR('#1.1: new Array(-1) throw RangeError. Actual: ' + (new Array(- 1)));}
  catch (e)
{    {
      var __result1 = (e instanceof RangeError) !== true;
      var __expect1 = false;
    }}

  try
{    new Array(4294967296);
    $ERROR('#2.1: new Array(4294967296) throw RangeError. Actual: ' + (new Array(4294967296)));}
  catch (e)
{    {
      var __result2 = (e instanceof RangeError) !== true;
      var __expect2 = false;
    }}

  try
{    new Array(4294967297);
    $ERROR('#3.1: new Array(4294967297) throw RangeError. Actual: ' + (new Array(4294967297)));}
  catch (e)
{    {
      var __result3 = (e instanceof RangeError) !== true;
      var __expect3 = false;
    }}

  