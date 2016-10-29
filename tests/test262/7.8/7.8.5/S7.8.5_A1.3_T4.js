  try
{    /u000D/.source;
    $ERROR('#1.1: RegularExpressionFirstChar :: Carriage Return is incorrect. Actual: ' + (/u000D/.source));}
  catch (e)
{    {
      var __result1 = (e instanceof SyntaxError) !== true;
      var __expect1 = false;
    }}

  