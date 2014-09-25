// DOM object models
(function() {

var __temp = HTMLTableElement.prototype.insertRow;
HTMLTableElement.prototype.insertRow= function (index) {
  // partial implementation
  if(index==-1){
    var x = document.createElement("tr");
    this.appendChild(x);
    return x;
  }
  else
   __temp.apply(this, arguments);
};


})();

