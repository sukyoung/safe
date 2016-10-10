  try
{    this.z;
    z;
    $ERROR('#1.1: this.z; z === undefined throw ReferenceError. Actual: ' + (z));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  