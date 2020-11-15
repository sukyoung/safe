QUnit.module('isInteger methods');
lodashStable.each([
    'isInteger',
    'isSafeInteger'
], function (methodName) {
    var func = _[methodName], isSafe = methodName == 'isSafeInteger';
    QUnit.test('`_.' + methodName + '` should return `true` for integer values', function (assert) {
        assert.expect(2);
        var values = [
                -1,
                0,
                1
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
                Object(1),
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
            return value === 0;
        });
        var actual = lodashStable.map(falsey, function (value, index) {
            return index ? func(value) : func();
        });
        assert.deepEqual(actual, expected);
        assert.strictEqual(func(args), __bool_top__);
        assert.strictEqual(func([
            1,
            2,
            3
        ]), false);
        assert.strictEqual(func(true), false);
        assert.strictEqual(func(new Date()), __bool_top__);
        assert.strictEqual(func(new Error()), __bool_top__);
        assert.strictEqual(func({ 'a': 1 }), false);
        assert.strictEqual(func(/x/), false);
        assert.strictEqual(func('a'), __bool_top__);
        assert.strictEqual(func(symbol), false);
    });
});