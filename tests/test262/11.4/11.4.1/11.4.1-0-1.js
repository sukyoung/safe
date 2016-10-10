  function testcase() 
  {
    var x = 1;
    var y = 2;
    var z = 3;
    if ((! delete x || delete y) && delete delete z)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  