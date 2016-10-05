  try
{    this();
    $ERROR('#1.1: this() throw TypeError. Actual: ' + (this()));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  