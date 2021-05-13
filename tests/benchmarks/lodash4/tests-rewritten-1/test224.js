QUnit.module('sortedIndex methods');
lodashStable.each([
    'sortedIndex',
    'sortedLastIndex'
], function (methodName) {
    var func = _[methodName], isSortedIndex = methodName == 'sortedIndex';
    QUnit.test('`_.' + methodName + '` should return the insert index', function (assert) {
        assert.expect(1);
        var array = [
                30,
                50
            ], values = [
                30,
                40,
                50
            ], expected = isSortedIndex ? [
                0,
                1,
                1
            ] : [
                1,
                1,
                2
            ];
        var actual = lodashStable.map(values, function (value) {
            return func(array, value);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should work with an array of strings', function (assert) {
        assert.expect(1);
        var array = [
                'a',
                'c'
            ], values = [
                'a',
                'b',
                'c'
            ], expected = isSortedIndex ? [
                0,
                1,
                1
            ] : [
                1,
                1,
                2
            ];
        var actual = lodashStable.map(values, function (value) {
            return func(array, value);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should accept a nullish `array` and a `value`', function (assert) {
        assert.expect(1);
        var values = [
                null,
                undefined
            ], expected = lodashStable.map(values, lodashStable.constant([
                0,
                0,
                0
            ]));
        var actual = lodashStable.map(values, function (array) {
            return [
                func(array, 1),
                func(array, undefined),
                func(array, NaN)
            ];
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should align with `_.sortBy`', function (assert) {
        assert.expect(12);
        var symbol1 = Symbol ? Symbol('a') : null, symbol2 = Symbol ? Symbol('b') : null, symbol3 = Symbol ? Symbol('c') : null, expected = [
                1,
                '2',
                {},
                symbol1,
                symbol2,
                null,
                undefined,
                NaN,
                NaN
            ];
        lodashStable.each([
            [
                NaN,
                symbol1,
                null,
                1,
                '2',
                {},
                symbol2,
                NaN,
                undefined
            ],
            [
                '2',
                null,
                1,
                symbol1,
                NaN,
                {},
                NaN,
                symbol2,
                undefined
            ]
        ], function (array) {
            assert.deepEqual(_.sortBy(array), expected);
            assert.strictEqual(func(expected, 3), 2);
            assert.strictEqual(func(expected, symbol3), isSortedIndex ? 3 : Symbol ? 5 : 6);
            assert.strictEqual(func(expected, null), isSortedIndex ? Symbol ? 5 : 3 : 6);
            assert.strictEqual(func(expected, undefined), isSortedIndex ? 6 : 7);
            assert.strictEqual(func(expected, NaN), isSortedIndex ? 7 : 9);
        });
    });
    QUnit.test('`_.' + methodName + '` should align with `_.sortBy` for nulls', function (assert) {
        assert.expect(3);
        var array = [
            null,
            null
        ];
        assert.strictEqual(func(array, null), isSortedIndex ? 0 : 2);
        assert.strictEqual(func(array, 1), 0);
        assert.strictEqual(func(array, 'a'), 0);
    });
    QUnit.test('`_.' + methodName + '` should align with `_.sortBy` for symbols', function (assert) {
        assert.expect(3);
        var symbol1 = Symbol ? Symbol('a') : null, symbol2 = Symbol ? Symbol('b') : null, symbol3 = Symbol ? Symbol('c') : null, array = [
                symbol1,
                symbol2
            ];
        assert.strictEqual(func(array, symbol3), isSortedIndex ? 0 : 2);
        assert.strictEqual(func(array, 1), 0);
        assert.strictEqual(func(array, 'a'), 0);
    });
});