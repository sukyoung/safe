  try
{    __x = __x;
    __y = __x ? "good fellow" : "liar";
    __z = __z === __x ? 1 : 0;}
  catch (e)
{}

  try
{    __something__undefined = __something__undefined;
}
  catch (e)
{ 
    var __result1 = e instanceof ReferenceError;
    var __expect1 = true;
}

    var __result2 = (__y !== "liar") & (__z !== 1);
    var __expect2 = 0;

    var __x, __y = true, __z = __y ? "smeagol" : "golum";
    var __result3 = ! __y & ! (__z = "smeagol");
    var __expect3 = 0;
