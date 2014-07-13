
function Polygon() {
  this.edges = 8;
}

function objectMasquerading () {

  function Rectangle(top_len,side_len) {
    this.temp = Polygon;
    this.temp();
    this.temp = null;
  }

  return new Rectangle(3, 5);
}

function sharedClassObject() {

  function Rectangle(top_len, side_len) {
    this.edges = 4;
    this.top = top_len;
    this.side = side_len;
    this.area = function area() { return this.top*this.side; };
  }

  Rectangle.prototype = new Polygon();

  return new Rectangle(3, 7);
}
var rec1 = objectMasquerading();

var rec2 = sharedClassObject();

//dumpObject(rec1);
var __result1 = rec1.edges;  // for SAFE
var __expect1 = 8;  // for SAFE

var __result2 = rec1.temp;  // for SAFE
var __expect2 = null;  // for SAFE

var __result3 = rec1 instanceof Polygon;  // for SAFE
var __expect3 = false;  // for SAFE

//dumpObject(rec2);
var __result4 = rec2.edges;  // for SAFE
var __expect4 = 4;  // for SAFE

var __result5 = rec2.top;  // for SAFE
var __expect5 = 3;  // for SAFE

var __result6 = rec2.side;  // for SAFE
var __expect6 = 7;  // for SAFE

var __result7 = rec2.area();  // for SAFE
var __expect7 = 21;  // for SAFE

var __result8 = rec2 instanceof Polygon;  // for SAFE
var __expect8 = true;  // for SAFE
