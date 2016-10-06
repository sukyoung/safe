  x = 1;
  {
    var __result1 = delete x !== true;
    var __expect1 = false;
  }
  function MyFunction() 
  {
    
  }
  ;
  MyFunction.prop = 1;
  {
    var __result2 = delete MyFunction.prop !== true;
    var __expect2 = false;
  }
  function MyFunction() 
  {
    
  }
  ;
  var MyObject = new MyFunction();
  MyObject.prop = 1;
  {
    var __result3 = delete MyObject.prop !== true;
    var __expect3 = false;
  }
  