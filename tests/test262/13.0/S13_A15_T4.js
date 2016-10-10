  THE_ANSWER = "Answer to Life, the Universe, and Everything";
  function __func() 
  {
    return typeof arguments;
    var arguments = THE_ANSWER;
  }
  ;
  {
    var __result1 = __func(42, 42, 42) !== "object";
    var __expect1 = false;
  }
  