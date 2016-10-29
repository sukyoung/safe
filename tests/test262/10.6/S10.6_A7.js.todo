  function f1() 
  {
    return arguments;
  }
  for(var i = 1;i < 5;i++)
  {
    if (f1(1, 2, 3, 4, 5)[i] !== (i + 1))
      $ERROR("#" + i + ": Returning function's arguments work wrong, f1(1,2,3,4,5)[" + i + "] !== " + (i + 1));
  }
  