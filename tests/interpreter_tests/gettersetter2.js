function Circle(radius) {
    this.radius = radius;
}

Circle.prototype = {
    get circumference() {
        return 2*Math.PI*this.radius;
    },

    get area() {
        return Math.PI*this.radius*this.radius;
    }
}

c = new Circle(10);
_<>_print(c.area); // Should output 314.159
_<>_print(c.circumference); // Should output 62.832

"PASS";
