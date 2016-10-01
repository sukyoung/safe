function testcase() 
{
  var sparseArray = [1, 2, , 4, , 6, ];
  var tempArray = [];
  for(var p in sparseArray)
  {
    if (sparseArray.hasOwnProperty(p))
    {
      tempArray.push(p);
    }
  }
  var returnedArray = Object.keys(sparseArray);
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
