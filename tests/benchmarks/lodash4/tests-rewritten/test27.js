QUnit.module('lodash.concat');
(function () {
    QUnit.test('should shallow clone `array`', function (assert) {
        assert.expect(2);
        var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ], actual = _.concat(array);
        assert.deepEqual(actual, array);
        assert.notStrictEqual(actual, array);
    });
    QUnit.test('should concat arrays and values', function (assert) {
        assert.expect(2);
        var array = [__num_top__], actual = _.concat(array, __num_top__, [__num_top__], [[__num_top__]]);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__,
            __num_top__,
            [__num_top__]
        ]);
        assert.deepEqual(array, [__num_top__]);
    });
    QUnit.test('should cast non-array `array` values to arrays', function (assert) {
        assert.expect(2);
        var values = [
            ,
            null,
            undefined,
            __bool_top__,
            __bool_top__,
            __num_top__,
            NaN,
            __str_top__
        ];
        var expected = lodashStable.map(values, function (value, index) {
            return index ? [value] : [];
        });
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.concat(value) : _.concat();
        });
        assert.deepEqual(actual, expected);
        expected = lodashStable.map(values, function (value) {
            return [
                value,
                __num_top__,
                [__num_top__]
            ];
        });
        actual = lodashStable.map(values, function (value) {
            return _.concat(value, [__num_top__], [[__num_top__]]);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should treat sparse arrays as dense', function (assert) {
        assert.expect(3);
        var expected = [], actual = _.concat(Array(__num_top__), Array(__num_top__));
        expected.push(undefined, undefined);
        assert.ok(__str_top__ in actual);
        assert.ok(__str_top__ in actual);
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should return a new wrapped array', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var array = [__num_top__], wrapped = _(array).concat([
                    __num_top__,
                    __num_top__
                ]), actual = wrapped.value();
            assert.deepEqual(array, [__num_top__]);
            assert.deepEqual(actual, [
                __num_top__,
                __num_top__,
                __num_top__
            ]);
        } else {
            skipAssert(assert, 2);
        }
    });
}());