  try
{    object[1];
    $ERROR('#1.1: object[1] throw ReferenceError. Actual: ' + (object[1]));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  try
{    object.prop;
    $ERROR('#2.1: object.prop throw ReferenceError. Actual: ' + (object.prop));}
  catch (e)
{    {
      var __result2 = (e instanceof ReferenceError) !== true;
      var __expect2 = false;
    }}

  