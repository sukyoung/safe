  function __func() 
  {
    is_undef = true;
    for (i = 0;i < arguments.length;i++)
    {
      delete arguments[i];
      is_undef = is_undef && (typeof arguments[i] === "undefined");
    }
    ;
    return is_undef;
  }
  ;
  {
    var __result1 = ! __func("A", "B", 1, 2);
    var __expect1 = false;
  }
  