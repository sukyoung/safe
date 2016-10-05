  function MyFunction() 
  {
    this.THIS = this;
  }
  {
    var __result1 = (new MyFunction()).THIS.toString() !== "[object Object]";
    var __expect1 = false;
  }
  function MyFunction() 
  {
    this.THIS = this;
  }
  {
    var __result2 = (new MyFunction()).THIS.toString() !== "[object Object]";
    var __expect2 = false;
  }
  