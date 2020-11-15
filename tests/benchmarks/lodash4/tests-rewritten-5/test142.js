QUnit.module('lodash.findLastIndex and lodash.lastIndexOf');
lodashStable.each([
    'findLastIndex',
    'lastIndexOf'
], function (methodName) {
    var array = [
            1,
            2,
            3,
            1,
            2,
            3
        ], func = _[methodName], resolve = methodName == 'findLastIndex' ? lodashStable.curry(lodashStable.eq) : identity;
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.strictEqual(func(array, resolve(3)), 5);
    });
    QUnit.test('`_.' + methodName + '` should work with a positive `fromIndex`', function (assert) {
        assert.expect(1);
        assert.strictEqual(func(array, resolve(1), __num_top__), 0);
    });
    QUnit.test('`_.' + methodName + '` should work with a `fromIndex` >= `length`', function (assert) {
        assert.expect(1);
        var values = [
                6,
                __num_top__,
                Math.pow(2, 32),
                Infinity
            ], expected = lodashStable.map(values, lodashStable.constant([
                -1,
                3,
                -1
            ]));
        var actual = lodashStable.map(values, function (fromIndex) {
            return [
                func(array, resolve(undefined), fromIndex),
                func(array, resolve(1), fromIndex),
                func(array, resolve(''), fromIndex)
            ];
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should work with a negative `fromIndex`', function (assert) {
        assert.expect(1);
        assert.strictEqual(func(array, resolve(2), -__num_top__), 1);
    });
    QUnit.test('`_.' + methodName + '` should work with a negative `fromIndex` <= `-length`', function (assert) {
        assert.expect(1);
        var values = [
                -6,
                -8,
                -Infinity
            ], expected = lodashStable.map(values, stubZero);
        var actual = lodashStable.map(values, function (fromIndex) {
            return func(array, resolve(1), fromIndex);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should treat falsey `fromIndex` values correctly', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, function (value) {
            return value === undefined ? 5 : -1;
        });
        var actual = lodashStable.map(falsey, function (fromIndex) {
            return func(array, resolve(__num_top__), fromIndex);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should coerce `fromIndex` to an integer', function (assert) {
        assert.expect(1);
        assert.strictEqual(func(array, resolve(2), 4.2), 4);
    });
});