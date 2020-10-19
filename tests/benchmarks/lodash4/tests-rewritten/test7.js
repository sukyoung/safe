QUnit.module('lodash.ary');
(function () {
    function fn(a, b, c) {
        return slice.call(arguments);
    }
    QUnit.test('should cap the number of arguments provided to `func`', function (assert) {
        assert.expect(2);
        var actual = lodashStable.map([
            __str_top__,
            __str_top__,
            __str_top__
        ], _.ary(parseInt, __num_top__));
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
        var capped = _.ary(fn, __num_top__);
        assert.deepEqual(capped(__str_top__, __str_top__, __str_top__, __str_top__), [
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should use `func.length` if `n` is not given', function (assert) {
        assert.expect(1);
        var capped = _.ary(fn);
        assert.deepEqual(capped(__str_top__, __str_top__, __str_top__, __str_top__), [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should treat a negative `n` as `0`', function (assert) {
        assert.expect(1);
        var capped = _.ary(fn, -__num_top__);
        try {
            var actual = capped(__str_top__);
        } catch (e) {
        }
        assert.deepEqual(actual, []);
    });
    QUnit.test('should coerce `n` to an integer', function (assert) {
        assert.expect(1);
        var values = [
                __str_top__,
                __num_top__,
                __str_top__
            ], expected = [
                [__str_top__],
                [__str_top__],
                []
            ];
        var actual = lodashStable.map(values, function (n) {
            var capped = _.ary(fn, n);
            return capped(__str_top__, __str_top__);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should not force a minimum argument count', function (assert) {
        assert.expect(1);
        var args = [
                __str_top__,
                __str_top__,
                __str_top__
            ], capped = _.ary(fn, __num_top__);
        var expected = lodashStable.map(args, function (arg, index) {
            return args.slice(__num_top__, index);
        });
        var actual = lodashStable.map(expected, function (array) {
            return capped.apply(undefined, array);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should use `this` binding of function', function (assert) {
        assert.expect(1);
        var capped = _.ary(function (a, b) {
                return this;
            }, __num_top__), object = { 'capped': capped };
        assert.strictEqual(object.capped(), object);
    });
    QUnit.test('should use the existing `ary` if smaller', function (assert) {
        assert.expect(1);
        var capped = _.ary(_.ary(fn, __num_top__), __num_top__);
        assert.deepEqual(capped(__str_top__, __str_top__, __str_top__), [__str_top__]);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var funcs = lodashStable.map([fn], _.ary), actual = funcs[__num_top__](__str_top__, __str_top__, __str_top__);
        assert.deepEqual(actual, [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should work when combined with other methods that use metadata', function (assert) {
        assert.expect(2);
        var array = [
                __str_top__,
                __str_top__,
                __str_top__
            ], includes = _.curry(_.rearg(_.ary(_.includes, __num_top__), __num_top__, __num_top__), __num_top__);
        assert.strictEqual(includes(__str_top__)(array, __num_top__), __bool_top__);
        if (!isNpm) {
            includes = _(_.includes).ary(__num_top__).rearg(__num_top__, __num_top__).curry(__num_top__).value();
            assert.strictEqual(includes(__str_top__)(array, __num_top__), __bool_top__);
        } else {
            skipAssert(assert);
        }
    });
}());