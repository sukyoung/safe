QUnit.module('lodash.curryRight');
(function () {
    function fn(a, b, c, d) {
        return slice.call(arguments);
    }
    QUnit.test('should curry based on the number of arguments given', function (assert) {
        assert.expect(3);
        var curried = _.curryRight(fn), expected = [
                1,
                2,
                3,
                4
            ];
        assert.deepEqual(curried(4)(3)(2)(1), expected);
        assert.deepEqual(curried(3, 4)(1, 2), expected);
        assert.deepEqual(curried(1, 2, 3, 4), expected);
    });
    QUnit.test('should allow specifying `arity`', function (assert) {
        assert.expect(3);
        var curried = _.curryRight(fn, 3), expected = [
                1,
                2,
                3
            ];
        assert.deepEqual(curried(__num_top__)(1, 2), expected);
        assert.deepEqual(curried(2, 3)(1), expected);
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
            return _.curryRight(fn, arity)();
        });
        assert.deepEqual(actual, expected);
        assert.deepEqual(_.curryRight(fn, '2')(1)(2), [
            2,
            1
        ]);
    });
    QUnit.test('should support placeholders', function (assert) {
        assert.expect(4);
        var curried = _.curryRight(fn), expected = [
                1,
                2,
                __num_top__,
                4
            ], ph = curried.placeholder;
        assert.deepEqual(curried(__num_top__)(2, ph)(1, ph)(3), expected);
        assert.deepEqual(curried(3, ph)(4)(__num_top__, ph)(2), expected);
        assert.deepEqual(curried(ph, ph, 4)(ph, 3)(ph, 2)(1), expected);
        assert.deepEqual(curried(ph, ph, ph, 4)(ph, ph, 3)(ph, __num_top__)(1), expected);
    });
    QUnit.test('should persist placeholders', function (assert) {
        assert.expect(1);
        var curried = _.curryRight(fn), ph = curried.placeholder, actual = curried('a', ph, ph, ph)('b')(ph)('c')('d');
        assert.deepEqual(actual, [
            'a',
            'b',
            'c',
            __str_top__
        ]);
    });
    QUnit.test('should use `_.placeholder` when set', function (assert) {
        assert.expect(1);
        if (!isModularize) {
            var curried = _.curryRight(fn), _ph = _.placeholder = {}, ph = curried.placeholder;
            assert.deepEqual(curried(4)(2, _ph)(1, ph), [
                1,
                2,
                ph,
                4
            ]);
            delete _.placeholder;
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should provide additional arguments after reaching the target arity', function (assert) {
        assert.expect(3);
        var curried = _.curryRight(fn, 3);
        assert.deepEqual(curried(4)(1, 2, 3), [
            1,
            2,
            3,
            4
        ]);
        assert.deepEqual(curried(4, 5)(1, 2, 3), [
            1,
            __num_top__,
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
            var curried = index ? _.curryRight(fn, 4) : _.curryRight(fn);
            assert.strictEqual(curried.length, 0);
            assert.strictEqual(curried(4).length, 0);
            assert.strictEqual(curried(3, 4).length, 0);
        });
    });
    QUnit.test('should ensure `new curried` is an instance of `func`', function (assert) {
        assert.expect(2);
        function Foo(value) {
            return value && object;
        }
        var curried = _.curryRight(Foo), object = {};
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
        assert.deepEqual(_.curryRight(_.bind(fn, object), 3)('c')('b')('a'), expected);
        assert.deepEqual(_.curryRight(_.bind(fn, object), 3)('b', 'c')('a'), expected);
        assert.deepEqual(_.curryRight(_.bind(fn, object), 3)('a', 'b', __str_top__), expected);
        assert.deepEqual(_.bind(_.curryRight(fn), object)('c')('b')(__str_top__), Array(3));
        assert.deepEqual(_.bind(_.curryRight(fn), object)('b', 'c')('a'), Array(3));
        assert.deepEqual(_.bind(_.curryRight(fn), object)('a', 'b', 'c'), expected);
        object.curried = _.curryRight(fn);
        assert.deepEqual(object.curried('c')('b')('a'), Array(3));
        assert.deepEqual(object.curried('b', 'c')('a'), Array(3));
        assert.deepEqual(object.curried('a', 'b', 'c'), expected);
    });
    QUnit.test('should work with partialed methods', function (assert) {
        assert.expect(2);
        var curried = _.curryRight(fn), expected = [
                1,
                2,
                3,
                4
            ];
        var a = _.partialRight(curried, __num_top__), b = _.partialRight(a, 3), c = _.bind(b, null, 1), d = _.partial(b(2), 1);
        assert.deepEqual(c(2), expected);
        assert.deepEqual(d(), expected);
    });
}());