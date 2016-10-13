  function testcase() 
  {
    function foo() 
    {
      
    }
    var o = {
      
    };
    var bf = foo.bind(o);
    var desc = Object.getOwnPropertyDescriptor(bf, 'length');
    if (desc.hasOwnProperty('value') === true && desc.hasOwnProperty('get') === false && desc.hasOwnProperty('set') === false)
    {
      return true;
    }
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  