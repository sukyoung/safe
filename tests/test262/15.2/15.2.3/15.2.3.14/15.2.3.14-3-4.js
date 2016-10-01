  function testcase() 
  {
    function testArgs2(x, y, z) 
    {
      var a = Object.keys(arguments);
      if (a.length === 2 && a[0] === "0" && a[1] === "1")
        return true;
    }
    function testArgs3(x, y, z) 
    {
      var a = Object.keys(arguments);
      if (a.length === 3 && a[0] === "0" && a[1] === "1" && a[2] === "2")
        return true;
    }
    function testArgs4(x, y, z) 
    {
      var a = Object.keys(arguments);
      if (a.length === 4 && a[0] === "0" && a[1] === "1" && a[2] === "2" && a[3] === "3")
        return true;
    }
    return testArgs2(1, 2) && testArgs3(1, 2, 3) && testArgs4(1, 2, 3, 4);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  