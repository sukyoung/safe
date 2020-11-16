QUnit.module('isInteger methods');
lodashStable.each([
    'isInteger',
    'isSafeInteger'
], function (methodName) {
    var func = _[methodName], isSafe = methodName == 'isSafeInteger';
    QUnit.test('`_.' + methodName + '` should return `true` for integer values', function (assert) {
        assert.expect(2);
        var values = [
                -__num_top__,
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
                3.14
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
        assert.strictEqual(func(args), false);
        assert.strictEqual(func([
            1,
            2,
            3
        ]), false);
        assert.strictEqual(func(true), false);
        assert.strictEqual(func(new Date()), false);
        assert.strictEqual(func(new Error()), false);
        assert.strictEqual(func({ 'a': 1 }), false);
        assert.strictEqual(func(/x/), false);
        assert.strictEqual(func('a'), false);
        assert.strictEqual(func(symbol), false);
    });
});