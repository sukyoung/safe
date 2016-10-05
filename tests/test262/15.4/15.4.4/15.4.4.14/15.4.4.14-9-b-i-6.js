  function testcase() 
  {
    try
{      Object.defineProperty(Object.prototype, "0", {
        get : (function () 
        {
          return false;
        }),
        configurable : true
      });
      return 0 === Array.prototype.indexOf.call({
        0 : true,
        1 : 1,
        length : 2
      }, 
      true);}
    finally
{      delete Object.prototype[0];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  