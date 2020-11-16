QUnit.module('lodash.curry');
(function () {
    function fn(a, b, c, d) {
        return slice.call(arguments);
    }
    QUnit.test('should curry based on the number of arguments given', function (assert) {
        assert.expect(3);
        var curried = _.curry(fn), expected = [
                1,
                __num_top__,
                3,
                4
            ];
        assert.deepEqual(curried(1)(2)(3)(4), expected);
        assert.deepEqual(curried(1, __num_top__)(3, 4), expected);
        assert.deepEqual(curried(1, 2, 3, 4), expected);
    });
    QUnit.test('should allow specifying `arity`', function (assert) {
        assert.expect(3);
        var curried = _.curry(fn, 3), expected = [
                1,
                2,
                3
            ];
        assert.deepEqual(curried(1)(2, 3), expected);
        assert.deepEqual(curried(1, 2)(3), expected);
        assert.deepEqual(curried(1, 2, 3), expected);
    });
    QUnit.test('should coerce `arity` to an integer', function (assert) {
        assert.expect(2);
        var values = [
                '0',
                0.6,
                'xyz'
            ], expected = lodashStable.map(values, stubArray);
        var actual = lodashStable.map(values, function (arity) {
            return _.curry(fn, arity)();
        });
        assert.deepEqual(actual, expected);
        assert.deepEqual(_.curry(fn, '2')(1)(__num_top__), [
            1,
            2
        ]);
    });
    QUnit.test('should support placeholders', function (assert) {
        assert.expect(4);
        var curried = _.curry(fn), ph = curried.placeholder;
        assert.deepEqual(curried(1)(ph, 3)(ph, 4)(2), [
            1,
            2,
            3,
            4
        ]);
        assert.deepEqual(curried(ph, 2)(1)(ph, 4)(3), [
            1,
            2,
            3,
            4
        ]);
        assert.deepEqual(curried(ph, ph, 3)(ph, 2)(ph, 4)(1), [
            1,
            2,
            3,
            4
        ]);
        assert.deepEqual(curried(ph, ph, ph, 4)(ph, ph, 3)(ph, 2)(1), [
            1,
            2,
            3,
            4
        ]);
    });
    QUnit.test('should persist placeholders', function (assert) {
        assert.expect(1);
        var curried = _.curry(fn), ph = curried.placeholder, actual = curried(ph, ph, ph, 'd')('a')(ph)('b')('c');
        assert.deepEqual(actual, [
            'a',
            'b',
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should use `_.placeholder` when set', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            var curried = _.curry(fn), _ph = _.placeholder = {}, ph = curried.placeholder;
            assert.deepEqual(curried(1)(_ph, 3)(ph, 4), [
                1,
                ph,
                3,
                4
            ]);
            delete _.placeholder;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should provide additional arguments after reaching the target arity', function (assert) {
        assert.expect(3);
        var curried = _.curry(fn, 3);
        assert.deepEqual(curried(__num_top__)(2, 3, 4), [
            1,
            2,
            3,
            4
        ]);
        assert.deepEqual(curried(1, 2)(3, 4, 5), [
            1,
            2,
            3,
            4,
            5
        ]);
        assert.deepEqual(curried(1, 2, 3, 4, 5, 6), [
            1,
            2,
            3,
            4,
            5,
            6
        ]);
    });
    QUnit.test('should create a function with a `length` of `0`', function (assert) {
        assert.expect(6);
        lodashStable.times(2, function (index) {
            var curried = index ? _.curry(fn, 4) : _.curry(fn);
            assert.strictEqual(curried.length, 0);
            assert.strictEqual(curried(1).length, 0);
            assert.strictEqual(curried(1, 2).length, 0);
        });
    });
    QUnit.test('should ensure `new curried` is an instance of `func`', function (assert) {
        assert.expect(2);
        function Foo(value) {
            return value && object;
        }
        var curried = _.curry(Foo), object = {};
        assert.ok(new curried(false) instanceof Foo);
        assert.strictEqual(new curried(true), object);
    });
    QUnit.test('should use `this` binding of function', function (assert) {
        assert.expect(9);
        var fn = function (a, b, c) {
            var value = this || {};
            return [
                value[a],
                value[b],
                value[c]
            ];
        };
        var object = {
                'a': 1,
                'b': 2,
                'c': 3
            }, expected = [
                1,
                2,
                3
            ];
        assert.deepEqual(_.curry(_.bind(fn, object), 3)('a')('b')(__str_top__), expected);
        assert.deepEqual(_.curry(_.bind(fn, object), 3)('a', 'b')('c'), expected);
        assert.deepEqual(_.curry(_.bind(fn, object), 3)('a', 'b', 'c'), expected);
        assert.deepEqual(_.bind(_.curry(fn), object)(__str_top__)('b')('c'), Array(3));
        assert.deepEqual(_.bind(_.curry(fn), object)('a', 'b')('c'), Array(3));
        assert.deepEqual(_.bind(_.curry(fn), object)('a', 'b', __str_top__), expected);
        object.curried = _.curry(fn);
        assert.deepEqual(object.curried('a')('b')('c'), Array(3));
        assert.deepEqual(object.curried('a', 'b')('c'), Array(3));
        assert.deepEqual(object.curried('a', 'b', 'c'), expected);
    });
    QUnit.test('should work with partialed methods', function (assert) {
        assert.expect(2);
        var curried = _.curry(fn), expected = [
                1,
                2,
                3,
                4
            ];
        var a = _.partial(curried, __num_top__), b = _.bind(a, null, 2), c = _.partialRight(b, 4), d = _.partialRight(b(3), 4);
        assert.deepEqual(c(3), expected);
        assert.deepEqual(d(), expected);
    });
}());