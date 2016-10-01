  function testcase() 
  {
    var props = {
      
    };
    props.prop1 = {
      value : 12,
      enumerable : true
    };
    props.prop2 = {
      value : true,
      enumerable : true
    };
    var tempArray = [];
    for(var p in props)
    {
      if (props.hasOwnProperty(p))
      {
        tempArray.push(p);
      }
    }
    var newObj = Object.create({
      
    }, props);
    var index = 0;
    for(var q in newObj)
    {
      if (tempArray[index++] !== q && newObj.hasOwnProperty(q))
      {
        return false;
      }
    }
    return true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  