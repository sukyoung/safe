function Name(first, last) {
  this.first = first;
  this.last = last;
}

Name.prototype = {
  get fullName() {
    return this.first + " " + this.last;
  },

  set fullName(name) {
    for(var i = 0; i < 10; i++) {
      if (name.charAt(i) == " ") {
        this.first = name.substring(0,i)
        this.last = name.substring(i+1)
        return;
      }
    }
  }
};

n = new Name("David", "Allen");
_<>_print(n.first);
_<>_print(n.last);
_<>_print(n.fullName);
n.fullName = "Jan Flood";
_<>_print(n.first);
_<>_print(n.last);
"PASS"
