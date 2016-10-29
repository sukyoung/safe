  try
{    /u2028/.source;
    $ERROR('#1.1: RegularExpressionFirstChar :: BackslashSequence :: \\Line separator is incorrect. Actual: ' + (/u2028/.source));}
  catch (e)
{    {
      var __result1 = (e instanceof SyntaxError) !== true;
      var __expect1 = false;
    }}

  