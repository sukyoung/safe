  MyFunct = (function () 
  {
    
  });
  __my__funct = new MyFunct;
  {
    var __result1 = ! (__my__funct instanceof MyFunct);
    var __expect1 = false;
  }
  {
    var __result2 = __my__funct instanceof Function;
    var __expect2 = false;
  }
  {
    var __result3 = ! (__my__funct instanceof Object);
    var __expect3 = false;
  }
  try
{    __my__funct instanceof __my__funct;
    $ERROR('#4 Only Function objects implement [[HasInstance]] and consequently can be proper ShiftExpression for The instanceof operator');}
  catch (e)
{    {
      var __result4 = e instanceof TypeError !== true;
      var __expect4 = false;
    }}

  