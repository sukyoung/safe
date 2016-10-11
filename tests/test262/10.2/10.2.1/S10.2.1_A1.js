  function f1(a, b) 
  {
    return (b === undefined);
  }
  if (! (f1(1, 2) === false))
  {
    $ERROR('#1: f1(1, 2) === false');
  }
  else
  {
    var __result1 = ! (f1(1) === true);
    var __expect1 = false;
  }
  function f2(a, b, c) 
  {
    return (b === undefined) && (c === undefined);
  }
  {
    var __result2 = ! (f2(1) === true);
    var __expect2 = false;
  }
  