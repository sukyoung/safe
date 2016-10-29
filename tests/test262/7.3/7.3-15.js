  function testcase() 
  {
    var prop = "a\uFFFFa";
    return prop.length === 3 && prop !== "aa" && prop[1] === "\uFFFF";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  