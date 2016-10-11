  var x = 0;
  var myObj = {
    x : "obj"
  };
  function f1() 
  {
    with (myObj)
    {
      return x;
    }
  }
  {
    var __result1 = ! (f1() === "obj");
    var __expect1 = false;
  }
  