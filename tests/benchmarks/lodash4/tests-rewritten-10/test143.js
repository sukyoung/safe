QUnit.module('indexOf methods');
lodashStable.each([
    'indexOf',
    'lastIndexOf',
    __str_top__,
    'sortedLastIndexOf'
], function (methodName) {
    var func = _[methodName], isIndexOf = !/last/i.test(methodName), isSorted = /^sorted/.test(methodName);
    QUnit.test('`_.' + methodName + '` should accept a falsey `array`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, lodashStable.constant(-1));
        var actual = lodashStable.map(falsey, function (array, index) {
            try {
                return index ? func(array) : func();
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should return `-1` for an unmatched value', function (assert) {
        assert.expect(5);
        var array = [
                1,
                2,
                3
            ], empty = [];
        assert.strictEqual(func(array, 4), -1);
        assert.strictEqual(func(array, 4, true), -1);
        assert.strictEqual(func(array, undefined, true), -1);
        assert.strictEqual(func(empty, undefined), -__num_top__);
        assert.strictEqual(func(empty, undefined, __bool_top__), -1);
    });
    QUnit.test('`_.' + methodName + '` should not match values on empty arrays', function (assert) {
        assert.expect(2);
        var array = [];
        array[-1] = 0;
        assert.strictEqual(func(array, undefined), -1);
        assert.strictEqual(func(array, 0, true), -1);
    });
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(3);
        var array = isSorted ? [
            __num_top__,
            __num_top__,
            NaN,
            NaN
        ] : [
            __num_top__,
            NaN,
            3,
            NaN,
            5,
            NaN
        ];
        if (isSorted) {
            assert.strictEqual(func(array, NaN, __bool_top__), isIndexOf ? 2 : 3);
            skipAssert(assert, 2);
        } else {
            assert.strictEqual(func(array, NaN), isIndexOf ? 1 : 5);
            assert.strictEqual(func(array, NaN, 2), isIndexOf ? 3 : 1);
            assert.strictEqual(func(array, NaN, -2), isIndexOf ? 5 : 3);
        }
    });
    QUnit.test('`_.' + methodName + '` should match `-0` as `0`', function (assert) {
        assert.expect(2);
        assert.strictEqual(func([-0], 0), 0);
        assert.strictEqual(func([0], -__num_top__), __num_top__);
    });
});