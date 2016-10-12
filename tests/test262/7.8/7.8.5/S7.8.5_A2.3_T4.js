  try
{    /au000D/.source;
    $ERROR('#1.1: RegularExpressionChar :: Carriage Retur is incorrect. Actual: ' + (/au000D/.source));}
  catch (e)
{    {
      var __result1 = (e instanceof SyntaxError) !== true;
      var __expect1 = false;
    }}

  