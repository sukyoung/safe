  {
    var __result1 = Number(new Number()) !== 0;
    var __expect1 = false;
  }
  {
    var __result2 = Number(new Number(0)) !== 0;
    var __expect2 = false;
  }
  {
    var __result3 = isNaN(Number(new Number(Number.NaN)) !== true);
    var __expect3 = false;
  }
  if (Number(new Number(null)) !== 0)
  {
    $ERROR('#4.1: Number(new Number(null)) === 0. Actual: ' + (Number(new Number(null))));
  }
  else
  {
    {
      var __result4 = 1 / Number(new Number(null)) !== Number.POSITIVE_INFINITY;
      var __expect4 = false;
    }
  }
  {
    var __result5 = isNaN(Number(new Number(void 0)) !== true);
    var __expect5 = false;
  }
  {
    var __result6 = Number(new Number(true)) !== 1;
    var __expect6 = false;
  }
  if (Number(new Number(false)) !== + 0)
  {
    $ERROR('#7.1: Number(new Number(false)) === 0. Actual: ' + (Number(new Number(false))));
  }
  else
  {
    {
      var __result7 = 1 / Number(new Number(false)) !== Number.POSITIVE_INFINITY;
      var __expect7 = false;
    }
  }
  {
    var __result8 = Number(new Boolean(true)) !== 1;
    var __expect8 = false;
  }
  if (Number(new Boolean(false)) !== + 0)
  {
    $ERROR('#9.1: Number(new Boolean(false)) === 0. Actual: ' + (Number(new Boolean(false))));
  }
  else
  {
    {
      var __result9 = 1 / Number(new Boolean(false)) !== Number.POSITIVE_INFINITY;
      var __expect9 = false;
    }
  }
  {
    var __result10 = isNaN(Number(new Array(2, 4, 8, 16, 32))) !== true;
    var __expect10 = false;
  }
  var myobj1 = {
    ToNumber : (function () 
    {
      return 12345;
    }),
    toString : (function () 
    {
      return "67890";
    }),
    valueOf : (function () 
    {
      return "[object MyObj]";
    })
  };
  {
    var __result11 = isNaN(Number(myobj1)) !== true;
    var __expect11 = false;
  }
  var myobj2 = {
    ToNumber : (function () 
    {
      return 12345;
    }),
    toString : (function () 
    {
      return "67890";
    }),
    valueOf : (function () 
    {
      return "9876543210";
    })
  };
  {
    var __result12 = Number(myobj2) !== 9876543210;
    var __expect12 = false;
  }
  var myobj3 = {
    ToNumber : (function () 
    {
      return 12345;
    }),
    toString : (function () 
    {
      return "[object MyObj]";
    })
  };
  {
    var __result13 = isNaN(Number(myobj3)) !== true;
    var __expect13 = false;
  }
  var myobj4 = {
    ToNumber : (function () 
    {
      return 12345;
    }),
    toString : (function () 
    {
      return "67890";
    })
  };
  {
    var __result14 = Number(myobj4) !== 67890;
    var __expect14 = false;
  }
  var myobj5 = {
    ToNumber : (function () 
    {
      return 12345;
    })
  };
  {
    var __result15 = isNaN(Number(myobj5)) !== true;
    var __expect15 = false;
  }
  