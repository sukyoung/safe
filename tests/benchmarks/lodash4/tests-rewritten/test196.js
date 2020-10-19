QUnit.module('lodash.pullAt');
(function () {
    QUnit.test('should modify the array and return removed elements', function (assert) {
        assert.expect(2);
        var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ], actual = _.pullAt(array, [
                __num_top__,
                __num_top__
            ]);
        assert.deepEqual(array, [__num_top__]);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should work with unsorted indexes', function (assert) {
        assert.expect(2);
        var array = [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
            ], actual = _.pullAt(array, [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
            ]);
        assert.deepEqual(array, [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ]);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should work with repeated indexes', function (assert) {
        assert.expect(2);
        var array = [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
            ], actual = _.pullAt(array, [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
            ]);
        assert.deepEqual(array, [__num_top__]);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should use `undefined` for nonexistent indexes', function (assert) {
        assert.expect(2);
        var array = [
                __str_top__,
                __str_top__,
                __str_top__
            ], actual = _.pullAt(array, [
                __num_top__,
                __num_top__,
                __num_top__
            ]);
        assert.deepEqual(array, [__str_top__]);
        assert.deepEqual(actual, [
            __str_top__,
            undefined,
            __str_top__
        ]);
    });
    QUnit.test('should flatten `indexes`', function (assert) {
        assert.expect(4);
        var array = [
            __str_top__,
            __str_top__,
            __str_top__
        ];
        assert.deepEqual(_.pullAt(array, __num_top__, __num_top__), [
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(array, [__str_top__]);
        array = [
            __str_top__,
            __str_top__,
            __str_top__,
            __str_top__
        ];
        assert.deepEqual(_.pullAt(array, [
            __num_top__,
            __num_top__
        ], __num_top__), [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(array, [__str_top__]);
    });
    QUnit.test('should return an empty array when no indexes are given', function (assert) {
        assert.expect(4);
        var array = [
                __str_top__,
                __str_top__,
                __str_top__
            ], actual = _.pullAt(array);
        assert.deepEqual(array, [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(actual, []);
        actual = _.pullAt(array, [], []);
        assert.deepEqual(array, [
            __str_top__,
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(actual, []);
    });
    QUnit.test('should work with non-index paths', function (assert) {
        assert.expect(2);
        var values = lodashStable.reject(empties, function (value) {
            return value === __num_top__ || lodashStable.isArray(value);
        }).concat(-__num_top__, __num_top__);
        var array = lodashStable.transform(values, function (result, value) {
            result[value] = __num_top__;
        }, []);
        var expected = lodashStable.map(values, stubOne), actual = _.pullAt(array, values);
        assert.deepEqual(actual, expected);
        expected = lodashStable.map(values, noop);
        actual = lodashStable.at(array, values);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should preserve the sign of `0`', function (assert) {
        assert.expect(1);
        var props = [
            -__num_top__,
            Object(-__num_top__),
            __num_top__,
            Object(__num_top__)
        ];
        var actual = lodashStable.map(props, function (key) {
            var array = [-__num_top__];
            array[__str_top__] = -__num_top__;
            return _.pullAt(array, key);
        });
        assert.deepEqual(actual, [
            [-__num_top__],
            [-__num_top__],
            [-__num_top__],
            [-__num_top__]
        ]);
    });
    QUnit.test('should support deep paths', function (assert) {
        assert.expect(3);
        var array = [];
        array.a = { 'b': __num_top__ };
        var actual = _.pullAt(array, __str_top__);
        assert.deepEqual(actual, [__num_top__]);
        assert.deepEqual(array.a, {});
        try {
            actual = _.pullAt(array, __str_top__);
        } catch (e) {
        }
        assert.deepEqual(actual, [undefined]);
    });
    QUnit.test('should work with a falsey `array` when keys are given', function (assert) {
        assert.expect(1);
        var values = falsey.slice(), expected = lodashStable.map(values, lodashStable.constant(Array(__num_top__)));
        var actual = lodashStable.map(values, function (array) {
            try {
                return _.pullAt(array, __num_top__, __num_top__, __str_top__, __str_top__);
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
}());