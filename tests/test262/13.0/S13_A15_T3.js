  THE_ANSWER = "Answer to Life, the Universe, and Everything";
  var arguments = THE_ANSWER;
  function __func(arguments) 
  {
    return arguments;
  }
  ;
  {
    var __result1 = typeof __func() !== "undefined";
    var __expect1 = false;
  }
  {
    var __result2 = __func("The Ultimate Question") !== "The Ultimate Question";
    var __expect2 = false;
  }
  