  for(var i = 0;i < 10;i++)
  {
    i *= 2;
    break;
  }
  if (i !== 0)
    $ERROR('#1: i === 0. Actual:  i ===' + i);
  for(var i = 0;i < 10;i++)
  {
    i *= 2;
    if (i === 3)
      $ERROR('#2: i !== 3');
  }
  