  try
{    var s1 = new Boolean();
    s1.valueOf = Number.prototype.valueOf;
    var v1 = s1.valueOf();
}
  catch (e)
{    {
      var __result1 = ! (e instanceof TypeError);
      var __expect1 = false;
    }}

  try
{    var s2 = new Boolean();
    s2.myValueOf = Number.prototype.valueOf;
    var v2 = s2.myValueOf();
}
  catch (e)
{    {
      var __result2 = ! (e instanceof TypeError);
      var __expect2 = false;
    }}

  
