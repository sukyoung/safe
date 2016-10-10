  try
{    if (true)
      (function () 
      {
        throw "instatement";
      })();
    $FAIL("#1 failed");}
  catch (e)
{    {
      var __result1 = e !== "instatement";
      var __expect1 = false;
    }}

  try
{    if (false)
      (function () 
      {
        throw "truebranch";
      })();
    (function () 
    {
      throw "missbranch";
    })();
    $FAIL("#2 failed");}
  catch (e)
{    {
      var __result2 = e !== "missbranch";
      var __expect2 = false;
    }}

  