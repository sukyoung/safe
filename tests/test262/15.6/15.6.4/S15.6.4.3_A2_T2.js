  try
{    var s1 = new Number();
    s1.valueOf = Boolean.prototype.valueOf;
    var v1 = s1.valueOf();
    $ERROR('#1: Boolean.prototype.valueOf on not a Boolean object should throw TypeError');}
  catch (e)
{    {
      var __result1 = ! (e instanceof TypeError);
      var __expect1 = false;
    }}

  try
{    var s2 = new Number();
    s2.myValueOf = Boolean.prototype.valueOf;
    var v2 = s2.myValueOf();
    $ERROR('#2: Boolean.prototype.valueOf on not a Boolean object should throw TypeError');}
  catch (e)
{    {
      var __result2 = ! (e instanceof TypeError);
      var __expect2 = false;
    }}

  