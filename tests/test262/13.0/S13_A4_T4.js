  function __func() 
  {
    return arguments[0].name + " " + arguments[0].surname;
  }
  ;
  {
    var __result1 = typeof __func !== "function";
    var __expect1 = false;
  }
  {
    var __result2 = __func({
      name : 'fox',
      surname : 'malder'
    }) !== "fox malder";
    var __expect2 = false;
  }
  function func__(arg) 
  {
    return arg.name + " " + arg.surname;
  }
  ;
  {
    var __result3 = typeof func__ !== "function";
    var __expect3 = false;
  }
  {
    var __result4 = func__({
      name : 'john',
      surname : 'lennon'
    }) !== "john lennon";
    var __expect4 = false;
  }
  