  try
{    /au000A/.source;
    $ERROR('#1.1: RegularExpressionChar :: Line Feedis incorrect. Actual: ' + (/au000A/.source));}
  catch (e)
{    {
      var __result1 = (e instanceof SyntaxError) !== true;
      var __expect1 = false;
    }}

  