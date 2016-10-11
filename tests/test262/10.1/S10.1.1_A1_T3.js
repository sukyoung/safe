  var x = new (function f1() 
  {
    return 1;
  });
  if (typeof (x.constructor) !== "function")
    $ERROR('#1: typeof(x.constructor)!=="function"');
  