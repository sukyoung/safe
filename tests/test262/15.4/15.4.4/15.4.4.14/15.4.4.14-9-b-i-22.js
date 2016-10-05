  function testcase() 
  {
    try
{      Object.defineProperty(Object.prototype, "0", {
        set : (function () 
        {
          
        }),
        configurable : true
      });
      return 0 === Array.prototype.indexOf.call({
        length : 1
      }, undefined);}
    finally
{      delete Object.prototype[0];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  