  var x = (function () 
  {
    throw "x";
  });
  var y = (function () 
  {
    throw "y";
  });
  function f_arg() 
  {
    
  }
  try
{    f_arg(x(), y());
    $ERROR('#1.1: var x = { valueOf: function () { throw "x"; } }; var y = { valueOf: function () { throw "y"; } }; function f_arg() {} f_arg(x(),y()) throw "x". Actual: ' + (f_arg(x(), y())));}
  catch (e)
{    if (e === "y")
    {
      $ERROR('#1.2: First argument is evaluated first, and then second argument');
    }
    else
    {
      {
        var __result1 = e !== "x";
        var __expect1 = false;
      }
    }}

  