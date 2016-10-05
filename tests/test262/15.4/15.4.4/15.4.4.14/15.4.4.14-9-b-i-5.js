  function testcase() 
  {
    try
{      Object.defineProperty(Array.prototype, "0", {
        get : (function () 
        {
          return false;
        }),
        configurable : true
      });
      return 0 === [true, ].indexOf(true);}
    finally
{      delete Array.prototype[0];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  