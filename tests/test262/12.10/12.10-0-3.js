  function testcase() 
  {
    var o = {
      prop : "12.10-0-3 before"
    };
    var f;
    with (o)
    {
      f = (function () 
      {
        return prop;
      });
    }
    o.prop = "12.10-0-3 after";
    return f() === "12.10-0-3 after";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  