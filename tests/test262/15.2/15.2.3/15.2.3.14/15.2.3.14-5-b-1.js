  function testcase() 
  {
    var obj = {
      prop1 : 100,
      prop2 : 200,
      prop3 : 300
    };
    var array = Object.keys(obj);
    var idx = 0;
    for(var index in array)
    {
      if (array.hasOwnProperty(index))
      {
        if (index !== idx.toString())
        {
          return false;
        }
        idx++;
      }
    }
    return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  