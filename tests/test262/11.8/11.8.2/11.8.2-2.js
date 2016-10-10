  function testcase() 
  {
    var accessed = false;
    var obj1 = {
      valueOf : (function () 
      {
        accessed = true;
        return 3;
      })
    };
    var obj2 = {
      toString : (function () 
      {
        if (accessed === true)
        {
          return 4;
        }
        else
        {
          return 2;
        }
      })
    };
    return ! (obj1 > obj2);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  