  function testcase() 
  {
    var arr = [0, 1, 2, 3, 4, ];
    return arr.indexOf(0) === arr.indexOf(0, 0) && arr.indexOf(2) === arr.indexOf(2, 0) && arr.indexOf(4) === arr.indexOf(4, 0);
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  