var myComparefn = (function (x, y) 
  {
    throw "error";
  });
  var x = [1, 0, ];
  try
{    x.sort(myComparefn);
}
  catch (e)
{    {
      var __result1 = e !== "error";
      var __expect1 = false;
    }}
