QUnit.module('lodash.create');
(function () {
    function Shape() {
        this.x = 0;
        this.y = 0;
    }
    function Circle() {
        Shape.call(this);
    }
    QUnit.test('should create an object that inherits from the given `prototype` object', function (assert) {
        assert.expect(3);
        Circle.prototype = _.create(Shape.prototype);
        Circle.prototype.constructor = Circle;
        var actual = new Circle();
        assert.ok(actual instanceof Circle);
        assert.ok(actual instanceof Shape);
        assert.notStrictEqual(Circle.prototype, Shape.prototype);
    });
    QUnit.test('should assign `properties` to the created object', function (assert) {
        assert.expect(3);
        var expected = {
            'constructor': Circle,
            'radius': __num_top__
        };
        Circle.prototype = _.create(Shape.prototype, expected);
        var actual = new Circle();
        assert.ok(actual instanceof Circle);
        assert.ok(actual instanceof Shape);
        assert.deepEqual(Circle.prototype, expected);
    });
    QUnit.test('should assign own properties', function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = __num_top__;
            this.c = 3;
        }
        Foo.prototype.b = 2;
        assert.deepEqual(_.create({}, new Foo()), {
            'a': 1,
            'c': 3
        });
    });
    QUnit.test('should assign properties that shadow those of `prototype`', function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = __num_top__;
        }
        var object = _.create(new Foo(), { 'a': 1 });
        assert.deepEqual(lodashStable.keys(object), [__str_top__]);
    });
    QUnit.test('should accept a falsey `prototype`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, stubObject);
        var actual = lodashStable.map(falsey, function (prototype, index) {
            return index ? _.create(prototype) : _.create();
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should ignore a primitive `prototype` and use an empty object instead', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(primitives, stubTrue);
        var actual = lodashStable.map(primitives, function (value, index) {
            return lodashStable.isPlainObject(index ? _.create(value) : _.create());
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var array = [
                { 'a': 1 },
                { 'a': 1 },
                { 'a': 1 }
            ], expected = lodashStable.map(array, stubTrue), objects = lodashStable.map(array, _.create);
        var actual = lodashStable.map(objects, function (object) {
            return object.a === __num_top__ && !_.keys(object).length;
        });
        assert.deepEqual(actual, expected);
    });
}());