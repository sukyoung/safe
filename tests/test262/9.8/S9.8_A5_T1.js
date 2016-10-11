  {
    var __result1 = String(new Number()) !== "0";
    var __expect1 = false;
  }
  {
    var __result2 = String(new Number(0)) !== "0";
    var __expect2 = false;
  }
  {
    var __result3 = String(new Number(Number.NaN)) !== "NaN";
    var __expect3 = false;
  }
  {
    var __result4 = String(new Number(null)) !== "0";
    var __expect4 = false;
  }
  {
    var __result5 = String(new Number(void 0)) !== "NaN";
    var __expect5 = false;
  }
  {
    var __result6 = String(new Number(true)) !== "1";
    var __expect6 = false;
  }
  {
    var __result7 = String(new Number(false)) !== "0";
    var __expect7 = false;
  }
  {
    var __result8 = String(new Boolean(true)) !== "true";
    var __expect8 = false;
  }
  {
    var __result9 = String(new Boolean(false)) !== "false";
    var __expect9 = false;
  }
  {
    var __result10 = String(new Array(2, 4, 8, 16, 32)) !== "2,4,8,16,32";
    var __expect10 = false;
  }
  var myobj1 = {
    toNumber : (function () 
    {
      return 12345;
    }),
    toString : (function () 
    {
      return 67890;
    }),
    valueOf : (function () 
    {
      return "[object MyObj]";
    })
  };
  {
    var __result11 = String(myobj1) !== "67890";
    var __expect11 = false;
  }
  var myobj2 = {
    toNumber : (function () 
    {
      return 12345;
    }),
    toString : (function () 
    {
      return {
        
      };
    }),
    valueOf : (function () 
    {
      return "[object MyObj]";
    })
  };
  {
    var __result12 = String(myobj2) !== "[object MyObj]";
    var __expect12 = false;
  }
  var myobj3 = {
    toNumber : (function () 
    {
      return 12345;
    }),
    valueOf : (function () 
    {
      return "[object MyObj]";
    })
  };
  {
    var __result13 = String(myobj3) !== "[object Object]";
    var __expect13 = false;
  }
  