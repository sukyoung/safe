  function __func() 
  {
    delete arguments;
    return arguments;
  }
  {
    var __result1 = typeof __func("A", "B", 1, 2) !== "object";
    var __expect1 = false;
  }
  