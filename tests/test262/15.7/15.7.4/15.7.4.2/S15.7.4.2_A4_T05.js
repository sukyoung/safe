  try
{    var s1 = {
      x : 1
    };
    s1.toString = Number.prototype.toString;
    var v1 = s1.toString();
}
  catch (e)
{    {
      var __result1 = ! (e instanceof TypeError);
      var __expect1 = false;
    }}

  try
{    var s2 = {
      x : 1
    };
    s2.myToString = Number.prototype.toString;
    var v2 = s2.myToString();
}
  catch (e)
{    {
      var __result2 = ! (e instanceof TypeError);
      var __expect2 = false;
    }}

  
