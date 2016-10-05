  try
{    throw "catchme";
    $ERROR('#1.1: throw "catchme" lead to throwing exception');}
  catch (e)
{    {
      var __result1 = delete e;
      var __expect1 = false;
    }
    {
      var __result2 = e !== "catchme";
      var __expect2 = false;
    }}

  try
{    throw "catchme";
    $ERROR('#2.1: throw "catchme" lead to throwing exception');}
  catch (e)
{    }

  try
{    e;
    $ERROR('#2.2: Deleting catching exception after ending "catch" block');}
  catch (err)
{    }

  