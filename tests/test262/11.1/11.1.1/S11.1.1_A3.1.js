  function MyFunction() 
  {
    return this;
  }
  {
    var __result1 = MyFunction() !== this;
    var __expect1 = false;
  }
  function MyFunction() 
  {
    return this;
  }
  {
    var __result2 = MyFunction() !== this;
    var __expect2 = false;
  }
  