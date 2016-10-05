  try
{    new x;
    $ERROR('#1.1: new x throw ReferenceError. Actual: ' + (new x));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  try
{    new x();
    $ERROR('#2: new x() throw ReferenceError');}
  catch (e)
{    {
      var __result2 = (e instanceof ReferenceError) !== true;
      var __expect2 = false;
    }}

  