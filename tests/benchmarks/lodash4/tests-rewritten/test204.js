QUnit.module('filter methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var array = [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ], func = _[methodName], isFilter = methodName == __str_top__, objects = [
            { 'a': __num_top__ },
            { 'a': __num_top__ }
        ];
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func([__num_top__], function (value, index, array) {
            array[index] = __num_top__;
            return isFilter;
        });
        assert.deepEqual(actual, [__num_top__]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.deepEqual(func(objects, __str_top__), [objects[isFilter ? __num_top__ : __num_top__]]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.deepEqual(func(objects, objects[__num_top__]), [objects[isFilter ? __num_top__ : __num_top__]]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var wrapped = _(array);
            var actual = wrapped[methodName](function (n) {
                return n < __num_top__;
            });
            assert.deepEqual(actual.value(), isFilter ? [
                __num_top__,
                __num_top__
            ] : [
                __num_top__,
                __num_top__
            ]);
            actual = wrapped[methodName](function (n) {
                return n > __num_top__;
            });
            assert.deepEqual(actual.value(), isFilter ? [
                __num_top__,
                __num_top__
            ] : [
                __num_top__,
                __num_top__
            ]);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var array = lodashStable.range(LARGE_ARRAY_SIZE + __num_top__), predicate = function (value) {
                    return isFilter ? isEven(value) : !isEven(value);
                };
            var object = lodashStable.zipObject(lodashStable.times(LARGE_ARRAY_SIZE, function (index) {
                return [
                    __str_top__ + index,
                    index
                ];
            }));
            var actual = _(array).slice(__num_top__).map(square)[methodName](predicate).value();
            assert.deepEqual(actual, _[methodName](lodashStable.map(array.slice(__num_top__), square), predicate));
            actual = _(object).mapValues(square)[methodName](predicate).value();
            assert.deepEqual(actual, _[methodName](lodashStable.mapValues(object, square), predicate));
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(5);
        if (!isNpm) {
            var args, array = lodashStable.range(LARGE_ARRAY_SIZE + __num_top__), expected = [
                    __num_top__,
                    __num_top__,
                    lodashStable.map(array.slice(__num_top__), square)
                ];
            _(array).slice(__num_top__)[methodName](function (value, index, array) {
                args || (args = slice.call(arguments));
            }).value();
            assert.deepEqual(args, [
                __num_top__,
                __num_top__,
                array.slice(__num_top__)
            ]);
            args = undefined;
            _(array).slice(__num_top__).map(square)[methodName](function (value, index, array) {
                args || (args = slice.call(arguments));
            }).value();
            assert.deepEqual(args, expected);
            args = undefined;
            _(array).slice(__num_top__).map(square)[methodName](function (value, index) {
                args || (args = slice.call(arguments));
            }).value();
            assert.deepEqual(args, expected);
            args = undefined;
            _(array).slice(__num_top__).map(square)[methodName](function (value) {
                args || (args = slice.call(arguments));
            }).value();
            assert.deepEqual(args, [__num_top__]);
            args = undefined;
            _(array).slice(__num_top__).map(square)[methodName](function () {
                args || (args = slice.call(arguments));
            }).value();
            assert.deepEqual(args, expected);
        } else {
            skipAssert(assert, 5);
        }
    });
});