
function Polygon() {
  this.area = function() {};
}

function Rectangle() {
    this.temp = Polygon;
    this.temp();
  }

function objectMasquerading () {
  return new Rectangle();
}

function Rectangle2() { }
Rectangle2.prototype = new Polygon();

function sharedClassObject() {
  return new Rectangle2();
}

var rec1 = objectMasquerading();
var rec2 = sharedClassObject();

//dumpObject(rec1);
var __result1 = rec1.area();  // for SAFE
var __expect1 = undefined;  // for SAFE

var __result2 = rec1 instanceof Polygon;  // for SAFE
var __expect2 = false;  // for SAFE

//dumpObject(rec2);
var __result3 = rec2.area();  // for SAFE
var __expect3 = undefined;  // for SAFE

var __result4 = rec2 instanceof Polygon;  // for SAFE
var __expect4 = true;  // for SAFE
