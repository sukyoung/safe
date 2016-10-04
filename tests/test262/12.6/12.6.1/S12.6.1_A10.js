  var check = 0;
  do
  {
    if (typeof (f) === "function")
    {
      check = - 1;
      break;
    }
    else
    {
      check = 1;
      break;
    }
  }while ((function f() 
  {
    
  }));
  {
    var __result1 = check !== 1;
    var __expect1 = false;
  }
  