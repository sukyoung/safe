QUnit.module('uniq methods');
lodashStable.each([
    'uniq',
    'uniqBy',
    'uniqWith',
    'sortedUniq',
    'sortedUniqBy'
], function (methodName) {
    var func = _[methodName], isSorted = /^sorted/.test(methodName), objects = [
            { 'a': 2 },
            { 'a': 3 },
            { 'a': 1 },
            { 'a': 2 },
            { 'a': 3 },
            { 'a': 1 }
        ];
    if (isSorted) {
        objects = _.sortBy(objects, 'a');
    } else {
        QUnit.test('`_.' + methodName + '` should return unique values of an unsorted array', function (assert) {
            assert.expect(1);
            var array = [
                2,
                1,
                2
            ];
            assert.deepEqual(func(array), [
                2,
                1
            ]);
        });
    }
    QUnit.test('`_.' + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var array = [
            1,
            2,
            2
        ];
        assert.deepEqual(func(array), [
            1,
            2
        ]);
    });
    QUnit.test('`_.' + methodName + '` should treat object instances as unique', function (assert) {
        assert.expect(1);
        assert.deepEqual(func(objects), objects);
    });
    QUnit.test('`_.' + methodName + '` should treat `-0` as `0`', function (assert) {
        assert.expect(1);
        var actual = lodashStable.map(func([
            -0,
            0
        ]), lodashStable.toString);
        assert.deepEqual(actual, ['0']);
    });
    QUnit.test('`_.' + methodName + '` should match `NaN`', function (assert) {
        assert.expect(1);
        assert.deepEqual(func([
            NaN,
            NaN
        ]), [NaN]);
    });
    QUnit.test('`_.' + methodName + '` should work with large arrays', function (assert) {
        assert.expect(1);
        var largeArray = [], expected = [
                0,
                {},
                'a'
            ], count = Math.ceil(LARGE_ARRAY_SIZE / expected.length);
        lodashStable.each(expected, function (value) {
            lodashStable.times(count, function () {
                largeArray.push(value);
            });
        });
        assert.deepEqual(func(largeArray), expected);
    });
    QUnit.test('`_.' + methodName + '` should work with large arrays of `-0` as `0`', function (assert) {
        assert.expect(1);
        var largeArray = lodashStable.times(LARGE_ARRAY_SIZE, function (index) {
            return isEven(index) ? -0 : 0;
        });
        var actual = lodashStable.map(func(largeArray), lodashStable.toString);
        assert.deepEqual(actual, ['0']);
    });
    QUnit.test('`_.' + methodName + '` should work with large arrays of boolean, `NaN`, and nullish values', function (assert) {
        assert.expect(1);
        var largeArray = [], expected = [
                null,
                undefined,
                false,
                true,
                NaN
            ], count = Math.ceil(LARGE_ARRAY_SIZE / expected.length);
        lodashStable.each(expected, function (value) {
            lodashStable.times(count, function () {
                largeArray.push(value);
            });
        });
        assert.deepEqual(func(largeArray), expected);
    });
    QUnit.test('`_.' + methodName + '` should work with large arrays of symbols', function (assert) {
        assert.expect(1);
        if (Symbol) {
            var largeArray = lodashStable.times(LARGE_ARRAY_SIZE, Symbol);
            assert.deepEqual(func(largeArray), largeArray);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.' + methodName + '` should work with large arrays of well-known symbols', function (assert) {
        assert.expect(1);
        if (Symbol) {
            var expected = [
                Symbol.hasInstance,
                Symbol.isConcatSpreadable,
                Symbol.iterator,
                Symbol.match,
                Symbol.replace,
                Symbol.search,
                Symbol.species,
                Symbol.split,
                Symbol.toPrimitive,
                Symbol.toStringTag,
                Symbol.unscopables
            ];
            var largeArray = [], count = Math.ceil(LARGE_ARRAY_SIZE / expected.length);
            expected = lodashStable.map(expected, function (symbol) {
                return symbol || {};
            });
            lodashStable.each(expected, function (value) {
                lodashStable.times(count, function () {
                    largeArray.push(value);
                });
            });
            assert.deepEqual(func(largeArray), expected);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('`_.' + methodName + '` should distinguish between numbers and numeric strings', function (assert) {
        assert.expect(1);
        var largeArray = [], expected = [
                '2',
                2,
                Object('2'),
                Object(2)
            ], count = Math.ceil(LARGE_ARRAY_SIZE / expected.length);
        lodashStable.each(expected, function (value) {
            lodashStable.times(count, function () {
                largeArray.push(value);
            });
        });
        assert.deepEqual(func(largeArray), expected);
    });
});