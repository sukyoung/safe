  this.p1 = 1;
  var result = "result";
  var value = "value";
  var myObj = {
    p1 : 'a',
    value : 'myObj_value',
    valueOf : (function () 
    {
      return 'obj_valueOf';
    })
  };
  try
{    var f = (function () 
    {
      throw value;
      p1 = 'x1';
    });
    with (myObj)
    {
      f();
    }}
  catch (e)
{    result = e;}

  {
    var __result1 = ! (p1 === 1);
    var __expect1 = false;
  }
  {
    var __result2 = ! (myObj.p1 === "a");
    var __expect2 = false;
  }
  {
    var __result3 = ! (result === "value");
    var __expect3 = false;
  }
  