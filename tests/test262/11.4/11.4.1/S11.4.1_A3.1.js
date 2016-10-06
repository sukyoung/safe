  var x = 1;
  {
    var __result1 = delete x !== false;
    var __expect1 = false;
  }
  var y = 1;
  {
    var __result2 = delete this.y !== false;
    var __expect2 = false;
  }
  function MyFunction() 
  {
    
  }
  ;
  {
    var __result3 = delete MyFunction !== false;
    var __expect3 = false;
  }
  function MyFunction() 
  {
    
  }
  ;
  var MyObject = new MyFunction();
  {
    var __result4 = delete MyObject !== false;
    var __expect4 = false;
  }
  {
    var __result5 = delete MyObject !== false;
    var __expect5 = false;
  }
  