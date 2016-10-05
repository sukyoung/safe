  try
{    "1"();
    $ERROR('#1.1: "1"() throw TypeError. Actual: ' + ("1"()));}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  try
{    var x = "1";
    x();
    $ERROR('#2.1: var x = "1"; x() throw TypeError. Actual: ' + (x()));}
  catch (e)
{    {
      var __result2 = (e instanceof TypeError) !== true;
      var __expect2 = false;
    }}

  