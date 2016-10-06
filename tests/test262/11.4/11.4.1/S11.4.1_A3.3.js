  try
{    x = 1;
    delete x;
    x;
    $ERROR('#1: x = 1; delete x; x is not exist');}
  catch (e)
{    {
      var __result1 = e instanceof ReferenceError !== true;
      var __expect1 = false;
    }}

  function MyFunction() 
  {
    
  }
  ;
  MyFunction.prop = 1;
  delete MyFunction.prop;
  {
    var __result2 = MyFunction.prop !== undefined;
    var __expect2 = false;
  }
  function MyFunction() 
  {
    
  }
  ;
  var MyObjectVar = new MyFunction();
  MyObjectVar.prop = 1;
  delete MyObjectVar.prop;
  {
    var __result3 = MyObjectVar.prop !== undefined;
    var __expect3 = false;
  }
  {
    var __result4 = delete MyObjectVar !== false;
    var __expect4 = false;
  }
  function MyFunction() 
  {
    
  }
  ;
  MyObjectNotVar = new MyFunction();
  MyObjectNotVar.prop = 1;
  delete MyObjectNotVar.prop;
  {
    var __result5 = MyObjectNotVar.prop !== undefined;
    var __expect5 = false;
  }
  {
    var __result6 = delete MyObjectNotVar !== true;
    var __expect6 = false;
  }
  