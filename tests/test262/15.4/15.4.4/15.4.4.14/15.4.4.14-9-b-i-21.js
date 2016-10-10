  function testcase() 
  {
    try
{      Object.defineProperty(Array.prototype, "0", {
        set : (function () 
        {
          
        }),
        configurable : true
      });
      return 0 === [, ].indexOf(undefined);}
    finally
{      delete Array.prototype[0];}

  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  