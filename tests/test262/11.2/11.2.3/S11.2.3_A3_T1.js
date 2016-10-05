  try
{    true();
    $ERROR('#1.1: true() throw TypeError. Actual: ' + (true()));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  try
{    var x = true;
    x();
    $ERROR('#2.1: var x = true; x() throw TypeError. Actual: ' + (x()));}
  catch (e)
{    {
      var __result2 = (e instanceof TypeError) !== true;
      var __expect2 = false;
    }}

  