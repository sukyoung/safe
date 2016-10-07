  try
{    throw 1;}
  catch (e)
{    {
      var __result1 = ! (e !== "1");
      var __expect1 = false;
    }}

  try
{    throw "1";}
  catch (e)
{    {
      var __result2 = ! (1 !== e);
      var __expect2 = false;
    }}

  