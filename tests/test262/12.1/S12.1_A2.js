  try
{    x();
}
  catch (e)
{
}

  try
{    throw "catchme";
}
  catch (e)
{    {
      var __result1 = e !== "catchme";
      var __expect1 = false;
    }}

  
