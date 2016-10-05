  var x = true;
  var object = {
    prop : x
  };
  {
    var __result1 = object.prop !== x;
    var __expect1 = false;
  }
  var x = new Boolean(true);
  var object = {
    prop : x
  };
  {
    var __result2 = object.prop !== x;
    var __expect2 = false;
  }
  var x = 1;
  var object = {
    prop : x
  };
  {
    var __result3 = object.prop !== x;
    var __expect3 = false;
  }
  var x = new Number(1);
  var object = {
    prop : x
  };
  {
    var __result4 = object.prop !== x;
    var __expect4 = false;
  }
  var x = "1";
  var object = {
    prop : x
  };
  {
    var __result5 = object.prop !== x;
    var __expect5 = false;
  }
  var x = new String(1);
  var object = {
    prop : x
  };
  {
    var __result6 = object.prop !== x;
    var __expect6 = false;
  }
  var x = undefined;
  var object = {
    prop : x
  };
  {
    var __result7 = object.prop !== x;
    var __expect7 = false;
  }
  var x = null;
  var object = {
    prop : x
  };
  {
    var __result8 = object.prop !== x;
    var __expect8 = false;
  }
  var x = {
    
  };
  var object = {
    prop : x
  };
  {
    var __result9 = object.prop !== x;
    var __expect9 = false;
  }
  var x = [1, 2, ];
  var object = {
    prop : x
  };
  {
    var __result10 = object.prop !== x;
    var __expect10 = false;
  }
  var x = (function () 
  {
    
  });
  var object = {
    prop : x
  };
  {
    var __result11 = object.prop !== x;
    var __expect11 = false;
  }
  var x = this;
  var object = {
    prop : x
  };
  {
    var __result12 = object.prop !== x;
    var __expect12 = false;
  }
  