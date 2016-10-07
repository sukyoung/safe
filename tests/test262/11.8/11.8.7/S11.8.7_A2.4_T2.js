  var x = (function () 
  {
    throw "x";
  });
  var y = (function () 
  {
    throw "y";
  });
  try
{    x() in y();
    $ERROR('#1.1: var x = function () { throw "x"; }; var y = function () { throw "y"; }; x() in y() throw "x". Actual: ' + (x() in y()));}
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

  