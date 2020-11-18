QUnit.module('round methods');
lodashStable.each([
    'ceil',
    'floor',
    'round'
], function (methodName) {
    var func = _[methodName], isCeil = methodName == 'ceil', isFloor = methodName == 'floor';
    QUnit.test('`_.' + methodName + '` should return a rounded number without a precision', function (assert) {
        assert.expect(1);
        var actual = func(4.006);
        assert.strictEqual(actual, isCeil ? 5 : 4);
    });
    QUnit.test('`_.' + methodName + '` should work with a precision of `0`', function (assert) {
        assert.expect(1);
        var actual = func(4.006, 0);
        assert.strictEqual(actual, isCeil ? 5 : 4);
    });
    QUnit.test('`_.' + methodName + '` should work with a positive precision', function (assert) {
        assert.expect(2);
        var actual = func(4.016, 2);
        assert.strictEqual(actual, isFloor ? 4.01 : 4.02);
        actual = func(4.1, 2);
        assert.strictEqual(actual, 4.1);
    });
    QUnit.test('`_.' + methodName + '` should work with a negative precision', function (assert) {
        assert.expect(1);
        var actual = func(4160, -2);
        assert.strictEqual(actual, isFloor ? 4100 : 4200);
    });
    QUnit.test('`_.' + methodName + '` should coerce `precision` to an integer', function (assert) {
        assert.expect(3);
        var actual = func(4.006, NaN);
        assert.strictEqual(actual, isCeil ? 5 : 4);
        var expected = isFloor ? 4.01 : 4.02;
        actual = func(4.016, 2.6);
        assert.strictEqual(actual, expected);
        actual = func(4.016, '+2');
        assert.strictEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should work with exponential notation and `precision`', function (assert) {
        assert.expect(3);
        var actual = func(50, 2);
        assert.deepEqual(actual, 50);
        actual = func('5e', 1);
        assert.deepEqual(actual, NaN);
        actual = func('5e1e1', 1);
        assert.deepEqual(actual, NaN);
    });
    QUnit.test('`_.' + methodName + '` should preserve the sign of `0`', function (assert) {
        assert.expect(1);
        var values = [
                [0],
                [-0],
                ['0'],
                ['-0'],
                [
                    0,
                    1
                ],
                [
                    -0,
                    1
                ],
                [
                    '0',
                    1
                ],
                [
                    '-0',
                    1
                ]
            ], expected = [
                Infinity,
                -Infinity,
                Infinity,
                -Infinity,
                Infinity,
                -Infinity,
                Infinity,
                -Infinity
            ];
        var actual = lodashStable.map(values, function (args) {
            return 1 / func.apply(undefined, args);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should not return `NaN` for large `precision` values', function (assert) {
        assert.expect(1);
        var results = [
            _.round(10.0000001, __num_top__),
            _.round(MAX_SAFE_INTEGER, 293)
        ];
        var expected = lodashStable.map(results, stubFalse), actual = lodashStable.map(results, lodashStable.isNaN);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should return `Infinity` given `Infinity` regardless of `precision`', function (assert) {
        assert.expect(6);
        var actual = func(Infinity);
        assert.strictEqual(actual, Infinity);
        actual = func(Infinity, 0);
        assert.strictEqual(actual, Infinity);
        actual = func(Infinity, 2);
        assert.strictEqual(actual, Infinity);
        actual = func(Infinity, -2);
        assert.strictEqual(actual, Infinity);
        actual = func(Infinity, 2);
        assert.strictEqual(actual, isFloor ? Infinity : Infinity);
        actual = func(Infinity, 2);
        assert.strictEqual(actual, isCeil ? Infinity : Infinity);
    });
    QUnit.test('`_.' + methodName + '` should return `-Infinity` given `-Infinity` regardless of `precision`', function (assert) {
        assert.expect(6);
        var actual = func(-Infinity);
        assert.strictEqual(actual, -Infinity);
        actual = func(-Infinity, 0);
        assert.strictEqual(actual, -Infinity);
        actual = func(-Infinity, 2);
        assert.strictEqual(actual, -Infinity);
        actual = func(-Infinity, -2);
        assert.strictEqual(actual, -Infinity);
        actual = func(-Infinity, 2);
        assert.strictEqual(actual, isFloor ? -Infinity : -Infinity);
        actual = func(-Infinity, 2);
        assert.strictEqual(actual, isCeil ? -Infinity : -Infinity);
    });
    QUnit.test('`_.' + methodName + '` should return `NaN` given `NaN` regardless of `precision`', function (assert) {
        assert.expect(6);
        var actual = func(NaN);
        assert.deepEqual(actual, NaN);
        actual = func(NaN, 0);
        assert.deepEqual(actual, NaN);
        actual = func(NaN, 2);
        assert.deepEqual(actual, NaN);
        actual = func(NaN, -2);
        assert.deepEqual(actual, NaN);
        actual = func(NaN, 2);
        assert.deepEqual(actual, isFloor ? NaN : NaN);
        actual = func(NaN, 2);
        assert.deepEqual(actual, isCeil ? NaN : NaN);
    });
});