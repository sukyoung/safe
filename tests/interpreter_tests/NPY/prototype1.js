var man = function(name) {
    this.name = name;
    this.getName = function() {
        return this.name;
    }   
    this.printName = function() {
        _<>_print(this.getName());
    }   
}
man.prototype.getName = function() { return "prototype getName" };
(new man("Hello")).printName();  // Hello
try {
(new man("Hello")).getNameProp();
} catch(e) {
_<>_print("no property");	// no property
}
man.prototype.getNameProp = function() { return this.propName() + " prototype getName" };
man.prototype.propName = function() { return this.name + " Prop" };
man.prototype.str = "protoStr";
_<>_print((new man("Hello")).getNameProp()); // Hello Prop prototype getName
_<>_print((new man("Hello")).propName()); // Hello Prop
_<>_print((new man("Hello")).str); // protoStr

