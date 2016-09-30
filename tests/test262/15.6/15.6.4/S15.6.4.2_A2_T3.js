  try
{    var s1 = new Date();
    s1.toString = Boolean.prototype.toString;
    var v1 = s1.toString();
    $ERROR('#1: Boolean.prototype.toString on not a Boolean object should throw TypeError');}
  catch (e)
{    {
      var __result1 = ! (e instanceof TypeError);
      var __expect1 = false;
    }}

  try
{    var s2 = new Date();
    s2.myToString = Boolean.prototype.toString;
    var v2 = s2.myToString();
    $ERROR('#2: Boolean.prototype.toString on not a Boolean object should throw TypeError');}
  catch (e)
{    {
      var __result2 = ! (e instanceof TypeError);
      var __expect2 = false;
    }}

  