  function testcase() 
  {
    var base = {
      
    };
    Object.defineProperty(base, "foo", {
      set : (function () 
      {
        ;
      })
    });
    var o = Object.create(base);
    return o.hasOwnProperty("foo") === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  