  try
{    x();
    $ERROR('#1.1: x() throw ReferenceError. Actual: ' + (x()));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  try
{    x(1, 2, 3);
    $ERROR('#2.1: x(1,2,3) throw ReferenceError. Actual: ' + (x(1, 2, 3)));}
  catch (e)
{    {
      var __result2 = (e instanceof ReferenceError) !== true;
      var __expect2 = false;
    }}

  