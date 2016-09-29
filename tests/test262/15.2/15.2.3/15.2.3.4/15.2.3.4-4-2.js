  function testcase() 
  {
    var result = Object.getOwnPropertyNames(Object);
    var found;
    return (
        0 <= result.indexOf('getPrototypeOf') &&
        0 <= result.indexOf('getOwnPropertyDescriptor') &&
        0 <= result.indexOf('getOwnPropertyNames') &&
        0 <= result.indexOf('create') &&
        0 <= result.indexOf('defineProperty') &&
        0 <= result.indexOf('defineProperties') &&
        0 <= result.indexOf('seal') &&
        0 <= result.indexOf('freeze') &&
        0 <= result.indexOf('preventExtensions') &&
        0 <= result.indexOf('isSealed') &&
        0 <= result.indexOf('isFrozen') &&
        0 <= result.indexOf('isExtensible') &&
        0 <= result.indexOf('keys') &&
        0 <= result.indexOf('prototype') &&
        0 <= result.indexOf('length')
    );
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  
