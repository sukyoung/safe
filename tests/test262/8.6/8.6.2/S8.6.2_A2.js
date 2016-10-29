  function FooObj() 
  {
    
  }
  ;
  FooObj.prototype.prop = "some";
  var foo = new FooObj;
  {
    var __result1 = foo.prop !== "some";
    var __expect1 = false;
  }
  foo.prop = true;
  var foo__ = new FooObj;
  {
    var __result2 = foo__.prop !== "some";
    var __expect2 = false;
  }
  