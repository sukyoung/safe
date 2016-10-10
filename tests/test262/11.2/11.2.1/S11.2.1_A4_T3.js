  if (typeof Function.prototype !== "function")
    $ERROR('#1: typeof Function.prototype === "function". Actual: ' + (typeof Function.prototype));
  if (typeof Function['prototype'] !== "function")
    $ERROR('#2: typeof Function["prototype"] === "function". Actual: ' + (typeof Function["prototype"]));
  if (typeof Function.prototype.toString !== "function")
    $ERROR('#3: typeof Function.prototype.toString === "function". Actual: ' + (typeof Function.prototype.toString));
  if (typeof Function.prototype['toString'] !== "function")
    $ERROR('#4: typeof Function.prototype["toString"] === "function". Actual: ' + (typeof Function.prototype["toString"]));
  if (typeof Function.prototype.length !== "number")
    $ERROR('#5: typeof Function.prototype.length === "number". Actual: ' + (typeof Function.prototype.length));
  if (typeof Function.prototype['length'] !== "number")
    $ERROR('#6: typeof Function.prototype["length"] === "number". Actual: ' + (typeof Function.prototype["length"]));
  if (typeof Function.prototype.valueOf !== "function")
    $ERROR('#7: typeof Function.prototype.valueOf === "function". Actual: ' + (typeof Function.prototype.valueOf));
  if (typeof Function.prototype['valueOf'] !== "function")
    $ERROR('#8: typeof Function.prototype["valueOf"] === "function". Actual: ' + (typeof Function.prototype["valueOf"]));
  