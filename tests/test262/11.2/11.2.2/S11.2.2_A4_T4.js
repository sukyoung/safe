  try
{    new this;
    $ERROR('#1: new this throw TypeError');}
  catch (e)
{    {
      var __result1 = (e instanceof TypeError) !== true;
      var __expect1 = false;
    }}

  try
{    new this();
    $ERROR('#2: new this() throw TypeError');}
  catch (e)
{    {
      var __result2 = (e instanceof TypeError) !== true;
      var __expect2 = false;
    }}

  