QUnit.module('isInteger methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isSafe = methodName == __str_top__;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var values = [
                -__num_top__,
                __num_top__,
                __num_top__
            ], expected = lodashStable.map(values, stubTrue);
        var actual = lodashStable.map(values, function (value) {
            return func(value);
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(func(MAX_INTEGER), !isSafe);
    });
    QUnit.test('should return `false` for non-integer number values', function (assert) {
        assert.expect(1);
        var values = [
                NaN,
                Infinity,
                -Infinity,
                Object(__num_top__),
                __num_top__
            ], expected = lodashStable.map(values, stubFalse);
        var actual = lodashStable.map(values, function (value) {
            return func(value);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return `false` for non-numeric values', function (assert) {
        assert.expect(10);
        var expected = lodashStable.map(falsey, function (value) {
            return value === __num_top__;
        });
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? func(value) : func();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(func(args), __bool_top__);
        assert.strictEqual(func([
            __num_top__,
            __num_top__,
            __num_top__
        ]), __bool_top__);
        assert.strictEqual(func(__bool_top__), __bool_top__);
        assert.strictEqual(func(new Date()), __bool_top__);
        assert.strictEqual(func(new Error()), __bool_top__);
        assert.strictEqual(func({ 'a': __num_top__ }), __bool_top__);
        assert.strictEqual(func(/x/), __bool_top__);
        assert.strictEqual(func(__str_top__), __bool_top__);
        assert.strictEqual(func(symbol), __bool_top__);
    });
});