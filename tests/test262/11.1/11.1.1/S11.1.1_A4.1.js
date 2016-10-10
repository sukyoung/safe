  var MyFunction = (function () 
  {
    return this;
  });
  {
    var __result1 = MyFunction() !== this;
    var __expect1 = false;
  }
  MyFunction = (function () 
  {
    return eval('this');
  });
  {
    var __result2 = MyFunction() !== this;
    var __expect2 = false;
  }
  