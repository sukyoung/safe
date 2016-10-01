function testcase() 
{
  var func = (function (a, b, c) 
  {
    return arguments;
  });
  var args = func(1, "b", false);
  var tempArray = [];
  for(var p in args)
  {
    if (args.hasOwnProperty(p))
    {
      tempArray.push(p);
    }
  }
  var returnedArray = Object.keys(args);
  for(var index in returnedArray)
  {
    if (tempArray[index] !== returnedArray[index])
    {
      return false;
    }
  }
  return true;
}
{
  var __result1 = testcase();
  var __expect1 = true;
}
