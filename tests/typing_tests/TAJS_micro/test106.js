function product(name, value) {
  this.name = name;
  if (value > 1000)
    this.value = 999;
  else
    this.value = value;
}

function prod_dept(name, value, dept)
{
  this.dept = dept;
  product.apply(this, arguments);
}
prod_dept.prototype = new product();

var cheese = new prod_dept("feta", 5, "food");

var car = new prod_dept("honda", 5000, "auto");

//dumpObject(cheese);
var __result1 = cheese.dept;  // for SAFE
var __expect1 = "food";  // for SAFE

var __result2 = cheese.name;  // for SAFE
var __expect2 = "feta";  // for SAFE

var __result3 = cheese.value;  // for SAFE
var __expect3 = 5.0;  // for SAFE

//dumpObject(car);
var __result4 = car.dept;  // for SAFE
var __expect4 = "auto";  // for SAFE

var __result5 = car.name;  // for SAFE
var __expect5 = "honda";  // for SAFE

var __result6 = car.value;  // for SAFE
var __expect6 = 999.0;  // for SAFE
