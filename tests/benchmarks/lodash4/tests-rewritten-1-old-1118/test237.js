QUnit.module('sum methods');
lodashStable.each([
    'sum',
    'sumBy'
], function (methodName) {
    var array = [
            6,
            4,
            2
        ], func = _[methodName];
    QUnit.test('`_.' + methodName + '` should return the sum of an array of numbers', function (assert) {
        assert.expect(1);
        assert.strictEqual(func(array), 12);
    });
    QUnit.test('`_.' + methodName + '` should return `0` when passing empty `array` values', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(empties, stubZero);
        var actual = lodashStable.map(empties, function (value) {
            return func(value);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should skip `undefined` values', function (assert) {
        assert.expect(1);
        assert.strictEqual(func([
            1,
            undefined
        ]), 1);
    });
    QUnit.test('`_.' + methodName + '` should not skip `NaN` values', function (assert) {
        assert.expect(1);
        assert.deepEqual(func([
            1,
            NaN
        ]), NaN);
    });
    QUnit.test('`_.' + methodName + '` should not coerce values to numbers', function (assert) {
        assert.expect(1);
        assert.strictEqual(func([
            '1',
            '2'
        ]), __str_top__);
    });
});