  function testcase() 
  {
    var b_num = Array.isArray(42);
    var b_undef = Array.isArray(undefined);
    var b_bool = Array.isArray(true);
    var b_str = Array.isArray("abc");
    var b_obj = Array.isArray({
      
    });
    var b_null = Array.isArray(null);
    if (b_num === false && b_undef === false && b_bool === false && b_str === false && b_obj === false && b_null === false)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  