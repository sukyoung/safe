QUnit.module('lodash.findIndex and lodash.indexOf');
lodashStable.each([
    'findIndex',
    'indexOf'
], function (methodName) {
    var array = [
            1,
            2,
            3,
            1,
            2,
            3
        ], func = _[methodName], resolve = methodName == 'findIndex' ? lodashStable.curry(lodashStable.eq) : identity;
    QUnit.test('`_.' + methodName + '` should return the index of the first matched value', function (assert) {
        assert.expect(1);
        assert.strictEqual(func(array, resolve(3)), 2);
    });
    QUnit.test('`_.' + methodName + '` should work with a positive `fromIndex`', function (assert) {
        assert.expect(1);
        assert.strictEqual(func(array, resolve(1), 2), 3);
    });
    QUnit.test('`_.' + methodName + '` should work with a `fromIndex` >= `length`', function (assert) {
        assert.expect(1);
        var values = [
                6,
                8,
                Math.pow(__num_top__, 32),
                Infinity
            ], expected = lodashStable.map(values, lodashStable.constant([
                -1,
                -__num_top__,
                -__num_top__
            ]));
        var actual = lodashStable.map(values, function (fromIndex) {
            return [
                func(array, resolve(undefined), fromIndex),
                func(array, resolve(1), fromIndex),
                func(array, resolve(__str_top__), fromIndex)
            ];
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should work with a negative `fromIndex`', function (assert) {
        assert.expect(1);
        assert.strictEqual(func(array, resolve(2), -__num_top__), 4);
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
    QUnit.test('`_.' + methodName + '` should treat falsey `fromIndex` values as `0`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, stubZero);
        var actual = lodashStable.map(falsey, function (fromIndex) {
            return func(array, resolve(1), fromIndex);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should coerce `fromIndex` to an integer', function (assert) {
        assert.expect(1);
        assert.strictEqual(func(array, resolve(2), 1.2), 1);
    });
});