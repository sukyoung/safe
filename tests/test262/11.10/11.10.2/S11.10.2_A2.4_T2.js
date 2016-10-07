  var x = (function () 
  {
    throw "x";
  });
  var y = (function () 
  {
    throw "y";
  });
  try
{    x() ^ y();
    $ERROR('#1.1: var x = function () { throw "x"; }; var y = function () { throw "y"; }; x() ^ y() throw "x". Actual: ' + (x() ^ y()));}
  catch (e)
{    if (e === "y")
    {
      $ERROR('#1.2: First expression is evaluated first, and then second expression');
    }
    else
    {
      {
        var __result1 = e !== "x";
        var __expect1 = false;
      }
    }}

  