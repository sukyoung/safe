  function testcase() 
  {
    var proto = {
      foo : 0
    };
    var Con = (function () 
    {
      
    });
    Con.prototype = proto;
    var child = new Con();
    child.foo = 10;
    Object.freeze(child);
    var desc = Object.getOwnPropertyDescriptor(child, "foo");
    delete child.foo;
    return child.foo === 10 && desc.configurable === false && desc.writable === false;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  