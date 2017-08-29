  function testcase() 
  {
    var bac = Array.bind(null);
    var a = bac(42);
    a.prop = "verifyPropertyExist";
    a[41] = 41;
    return a.prop === "verifyPropertyExist" && a[41] === 41 && a.length === 42;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  