  function testcase() 
  {
    var o = {
      
    };
    Object.defineProperty(o, "foo", {
      get : (function () 
      {
        return 5;
      }),
      configurable : true
    });
    Object.preventExtensions(o);
    Object.defineProperty(o, "foo", {
      value : "hello",
      writable : true
    });
    var fooDescrip = Object.getOwnPropertyDescriptor(o, "foo");
    return o.foo === "hello" && fooDescrip.get === undefined && fooDescrip.set === undefined && fooDescrip.value === "hello" && fooDescrip.configurable === true && fooDescrip.enumerable === false && fooDescrip.writable === true;
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  