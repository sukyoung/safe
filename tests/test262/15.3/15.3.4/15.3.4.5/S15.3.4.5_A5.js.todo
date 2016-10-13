  function construct(f, args) 
  {
    var bound = Function.prototype.bind.apply(f, [null, ].concat(args));
    return new bound();
  }
  var d = construct(Date, [1957, 4, 27, ]);
  {
    var __result1 = Object.prototype.toString.call(d) !== '[object Date]';
    var __expect1 = false;
  }
  