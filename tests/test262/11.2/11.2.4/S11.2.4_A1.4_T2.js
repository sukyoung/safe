  function f_arg() 
  {
    
  }
  try
{    f_arg(x, x = 1);
    $ERROR('#1.1: function f_arg() {} f_arg(x,x=1) throw ReferenceError. Actual: ' + (f_arg(x, x = 1)));}
  catch (e)
{    {
      var __result1 = (e instanceof ReferenceError) !== true;
      var __expect1 = false;
    }}

  