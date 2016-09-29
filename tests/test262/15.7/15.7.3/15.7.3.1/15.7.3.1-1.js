  function testcase() 
  {
    var d = Object.getOwnPropertyDescriptor(Number, 'prototype');
    if (d.writable === false && d.enumerable === false && d.configurable === false)
    {
      return true;
    }
  }
var __result1 = testcase();
var __expect1 = true;
