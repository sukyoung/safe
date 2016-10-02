  function __func() 
  {
    was_del = false;
    for (i = 0;i < arguments.length;i++)
      was_del = was_del || delete arguments[i];
    return was_del;
  }
  {
    var __result1 = ! __func("A", "B", 1, 2);
    var __expect1 = false;
  }
  