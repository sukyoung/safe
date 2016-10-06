  var object = {
    valueOf : (function () 
    {
      return 1;
    })
  };
  {
    var __result1 = ! object !== false;
    var __expect1 = false;
  }
  var object = {
    valueOf : (function () 
    {
      return 1;
    }),
    toString : (function () 
    {
      return 0;
    })
  };
  {
    var __result2 = ! object !== false;
    var __expect2 = false;
  }
  var object = {
    valueOf : (function () 
    {
      return 1;
    }),
    toString : (function () 
    {
      return {
        
      };
    })
  };
  {
    var __result3 = ! object !== false;
    var __expect3 = false;
  }
  var object = {
    valueOf : (function () 
    {
      return 1;
    }),
    toString : (function () 
    {
      throw "error";
    })
  };
  {
    var __result4 = ! object !== false;
    var __expect4 = false;
  }
  var object = {
    toString : (function () 
    {
      return 1;
    })
  };
  {
    var __result5 = ! object !== false;
    var __expect5 = false;
  }
  var object = {
    valueOf : (function () 
    {
      return {
        
      };
    }),
    toString : (function () 
    {
      return 1;
    })
  };
  {
    var __result6 = ! object !== false;
    var __expect6 = false;
  }
  var object = {
    valueOf : (function () 
    {
      throw "error";
    }),
    toString : (function () 
    {
      return 1;
    })
  };
  {
    var __result7 = ! object !== false;
    var __expect7 = false;
  }
  var object = {
    valueOf : (function () 
    {
      return {
        
      };
    }),
    toString : (function () 
    {
      return {
        
      };
    })
  };
  {
    var __result8 = ! object !== false;
    var __expect8 = false;
  }
  