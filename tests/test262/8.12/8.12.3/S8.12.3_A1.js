  function FooObj() 
  {
    
  }
  ;
  FooObj.prototype.propFoo = "some";
  var __obj = new FooObj;
  {
    var __result1 = __obj.propFoo !== "some";
    var __expect1 = false;
  }
  {
    var __result2 = __obj['propFoo'] !== "some";
    var __expect2 = false;
  }
  