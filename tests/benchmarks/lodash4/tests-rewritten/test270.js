QUnit.module('uniq methods');
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isSorted = /^sorted/.test(methodName), objects = [
            { 'a': __num_top__ },
            { 'a': __num_top__ },
            { 'a': __num_top__ },
            { 'a': __num_top__ },
            { 'a': __num_top__ },
            { 'a': __num_top__ }
        ];
    if (isSorted) {
        objects = _.sortBy(objects, __str_top__);
    } else {
        QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
            assert.expect(1);
            var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ];
            assert.deepEqual(func(array), [
                __num_top__,
                __num_top__
            ]);
        });
    }
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var array = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        assert.deepEqual(func(array), [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.deepEqual(func(objects), objects);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = lodashStable.map(func([
            -__num_top__,
            __num_top__
        ]), lodashStable.toString);
        assert.deepEqual(actual, [__str_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.deepEqual(func([
            NaN,
            NaN
        ]), [NaN]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var largeArray = [], expected = [
                __num_top__,
                {},
                __str_top__
            ], count = Math.ceil(LARGE_ARRAY_SIZE / expected.length);
        lodashStable.each(expected, function (value) {
            lodashStable.times(count, function () {
                largeArray.push(value);
            });
        });
        assert.deepEqual(func(largeArray), expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var largeArray = lodashStable.times(LARGE_ARRAY_SIZE, function (index) {
            return isEven(index) ? -__num_top__ : __num_top__;
        });
        var actual = lodashStable.map(func(largeArray), lodashStable.toString);
        assert.deepEqual(actual, [__str_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var largeArray = [], expected = [
                null,
                undefined,
                __bool_top__,
                __bool_top__,
                NaN
            ], count = Math.ceil(LARGE_ARRAY_SIZE / expected.length);
        lodashStable.each(expected, function (value) {
            lodashStable.times(count, function () {
                largeArray.push(value);
            });
        });
        assert.deepEqual(func(largeArray), expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (Symbol) {
            var largeArray = lodashStable.times(LARGE_ARRAY_SIZE, Symbol);
            assert.deepEqual(func(largeArray), largeArray);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
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
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var largeArray = [], expected = [
                __str_top__,
                __num_top__,
                Object(__str_top__),
                Object(__num_top__)
            ], count = Math.ceil(LARGE_ARRAY_SIZE / expected.length);
        lodashStable.each(expected, function (value) {
            lodashStable.times(count, function () {
                largeArray.push(value);
            });
        });
        assert.deepEqual(func(largeArray), expected);
    });
});