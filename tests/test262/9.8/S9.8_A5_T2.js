  {
    var __result1 = new Number() + "" !== "0";
    var __expect1 = false;
  }
  {
    var __result2 = new Number(0) + "" !== "0";
    var __expect2 = false;
  }
  {
    var __result3 = new Number(Number.NaN) + "" !== "NaN";
    var __expect3 = false;
  }
  {
    var __result4 = new Number(null) + "" !== "0";
    var __expect4 = false;
  }
  {
    var __result5 = new Number(void 0) + "" !== "NaN";
    var __expect5 = false;
  }
  {
    var __result6 = new Number(true) + "" !== "1";
    var __expect6 = false;
  }
  {
    var __result7 = new Number(false) + "" !== "0";
    var __expect7 = false;
  }
  {
    var __result8 = new Boolean(true) + "" !== "true";
    var __expect8 = false;
  }
  {
    var __result9 = new Boolean(false) + "" !== "false";
    var __expect9 = false;
  }
  {
    var __result10 = new Array(2, 4, 8, 16, 32) + "" !== "2,4,8,16,32";
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
    var __result11 = myobj1 + "" !== "[object MyObj]";
    var __expect11 = false;
  }
  var myobj2 = {
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
      return {
        
      };
    })
  };
  {
    var __result12 = myobj2 + "" !== "67890";
    var __expect12 = false;
  }
  var myobj3 = {
    toNumber : (function () 
    {
      return 12345;
    })
  };
  {
    var __result13 = myobj3 + "" !== "[object Object]";
    var __expect13 = false;
  }
  