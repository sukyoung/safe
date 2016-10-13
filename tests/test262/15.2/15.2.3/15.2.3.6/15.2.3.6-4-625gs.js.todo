  Object.defineProperty(Object.prototype, "prop", {
    value : 1001,
    writable : false,
    enumerable : false,
    configurable : false
  });
  var prop = 1002;
  if (! (this.hasOwnProperty("prop") && prop === 1002))
  {
    throw "this.prop should take precedence over Object.prototype.prop";
  }
  