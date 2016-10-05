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
  var theirObj = {
    p1 : true,
    value : 'theirObj_value',
    valueOf : (function () 
    {
      return 'thr_valueOf';
    })
  };
  with (myObj)
  {
    with (theirObj)
    {
      p1 = 'x1';
    }
  }
  {
    var __result1 = p1 !== 1;
    var __expect1 = false;
  }
  {
    var __result2 = myObj.p1 !== "a";
    var __expect2 = false;
  }
  {
    var __result3 = theirObj.p1 !== "x1";
    var __expect3 = false;
  }
  