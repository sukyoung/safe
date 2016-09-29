function testcase() 
{
  function base() 
  {

  }
  function derived() 
  {

  }
  derived.prototype = new base();
  var d = new derived();
  var x = Object.getPrototypeOf(d);
  if (x.isPrototypeOf(d) === true)
  {
    return true;
  }
}

var __result1 = testcase();
var __expect1 = true;
