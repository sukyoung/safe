function Polygon() {}

function objectMasquerading () {
  function Rectangle() {
    Polygon();
  }
  return new Rectangle();
}

var rec1 = objectMasquerading();
//dumpObject(rec1);
var __result1 = rec1 instanceof Polygon;  // for SAFE
var __expect1 = false;  // for SAFE
