function Polygon() {
}

function Rectangle() {
    this.temp = Polygon;
    this.temp();
  }

new Polygon();
var rec1 = new Rectangle();

//dumpObject(rec1);
var __result1 = rec1 instanceof Rectangle;  // for SAFE
var __expect1 = true;  // for SAFE

var __result2 = rec1 instanceof Polygon;  // for SAFE
var __expect2 = false;  // for SAFE
