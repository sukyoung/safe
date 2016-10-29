  var __obj = {
    
  };
  {
    var __result1 = ! Object.prototype.isPrototypeOf(__obj);
    var __expect1 = false;
  }
  ;
  var protoObj = {
    
  };
  function FooObj() 
  {
    
  }
  ;
  var obj__ = new FooObj;
  {
    var __result2 = ! Object.prototype.isPrototypeOf(obj__);
    var __expect2 = false;
  }
  ;
  {
    var __result3 = ! FooObj.prototype.isPrototypeOf(obj__);
    var __expect3 = false;
  }
  ;
  {
    var __result4 = protoObj.isPrototypeOf(obj__);
    var __expect4 = false;
  }
  ;
  FooObj.prototype = protoObj;
  {
    var __result5 = protoObj.isPrototypeOf(obj__);
    var __expect5 = false;
  }
  ;
  var __foo = new FooObj;
  {
    var __result6 = ! Object.prototype.isPrototypeOf(__foo);
    var __expect6 = false;
  }
  ;
  {
    var __result7 = ! FooObj.prototype.isPrototypeOf(__foo);
    var __expect7 = false;
  }
  ;
  {
    var __result8 = ! protoObj.isPrototypeOf(__foo);
    var __expect8 = false;
  }
  ;
  