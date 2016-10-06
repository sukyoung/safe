  function MyFunction() 
  {
    
  }
  var MyObject = new MyFunction();
  {
    var __result1 = delete MyObject.prop !== true;
    var __expect1 = false;
  }
  var MyObject = new Object();
  {
    var __result2 = delete MyObject.prop !== true;
    var __expect2 = false;
  }
  