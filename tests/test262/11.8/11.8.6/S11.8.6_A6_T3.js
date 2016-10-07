  function MyFunct() 
  {
    return 0;
  }
  ;
  {
    var __result1 = MyFunct instanceof MyFunct;
    var __expect1 = false;
  }
  {
    var __result2 = MyFunct instanceof Function !== true;
    var __expect2 = false;
  }
  {
    var __result3 = MyFunct instanceof Object !== true;
    var __expect3 = false;
  }
  