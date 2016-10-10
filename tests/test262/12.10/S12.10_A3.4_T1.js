  this.p1 = 1;
  var result = "result";
  var myObj = {
    p1 : 'a',
    value : 'myObj_value',
    valueOf : (function () 
    {
      return 'obj_valueOf';
    })
  };
  do
  {
    with (myObj)
    {
      p1 = 'x1';
    }
  }while (false);
  {
    var __result1 = p1 !== 1;
    var __expect1 = false;
  }
  {
    var __result2 = myObj.p1 !== "x1";
    var __expect2 = false;
  }
  