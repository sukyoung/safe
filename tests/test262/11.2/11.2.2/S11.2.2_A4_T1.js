  try
{    new new Boolean(true);
    $ERROR('#1: new new Boolean(true) throw TypeError');}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  try
{    var x = new Boolean(true);
    new x;
    $ERROR('#2: var x = new Boolean(true); new x throw TypeError');}
  catch (e)
{    {
      var __result2 = (e instanceof TypeError) !== true;
      var __expect2 = false;
    }}

  try
{    var x = new Boolean(true);
    new x();
    $ERROR('#3: var x = new Boolean(true); new x() throw TypeError');}
  catch (e)
{    {
      var __result3 = (e instanceof TypeError) !== true;
      var __expect3 = false;
    }}

  