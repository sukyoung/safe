  var MyFunction = (function () 
  {
    this.THIS = this;
  });
  var MyObject = new MyFunction();
  {
    var __result1 = MyObject.THIS.toString() !== "[object Object]";
    var __expect1 = false;
  }
  MyFunction = (function () 
  {
    this.THIS = eval('this');
  });
  MyObject = new MyFunction();
  {
    var __result2 = MyObject.THIS.toString() !== "[object Object]";
    var __expect2 = false;
  }
  