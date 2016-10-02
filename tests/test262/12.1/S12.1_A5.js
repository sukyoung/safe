  try
{    throw 1;
    throw 2;
    throw 3;
    $ERROR('1.1: throw 1 lead to throwing exception');}
  catch (e)
{    {
      var __result1 = e !== 1;
      var __expect1 = false;
    }}

  try
{    {
      throw 1;
      throw 2;
    }
    throw 3;
    $ERROR('#2.1: throw 1 lead to throwing exception');}
  catch (e)
{    {
      var __result2 = e !== 1;
      var __expect2 = false;
    }}

  try
{    throw 1;
    {
      throw 2;
      throw 3;
    }
    $ERROR('#3.1: throw 1 lead to throwing exception');}
  catch (e)
{    {
      var __result3 = e !== 1;
      var __expect3 = false;
    }}

  