  try
{    Math();
    $ERROR('#1.1: Math() throw TypeError. Actual: ' + (Math()));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  