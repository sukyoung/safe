QUnit.module('lodash.zipWith');
(function () {
    QUnit.test('should zip arrays combining grouped elements with `iteratee`', function (assert) {
        assert.expect(2);
        var array1 = [
                __num_top__,
                __num_top__,
                __num_top__
            ], array2 = [
                __num_top__,
                __num_top__,
                __num_top__
            ], array3 = [
                __num_top__,
                __num_top__,
                __num_top__
            ];
        var actual = _.zipWith(array1, array2, array3, function (a, b, c) {
            return a + b + c;
        });
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
        var actual = _.zipWith(array1, [], function (a, b) {
            return a + (b || __num_top__);
        });
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should provide correct `iteratee` arguments', function (assert) {
        assert.expect(1);
        var args;
        _.zipWith([
            __num_top__,
            __num_top__
        ], [
            __num_top__,
            __num_top__
        ], [
            __num_top__,
            __num_top__
        ], function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should perform a basic zip when `iteratee` is nullish', function (assert) {
        assert.expect(1);
        var array1 = [
                __num_top__,
                __num_top__
            ], array2 = [
                __num_top__,
                __num_top__
            ], values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, lodashStable.constant(_.zip(array1, array2)));
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.zipWith(array1, array2, value) : _.zipWith(array1, array2);
        });
        assert.deepEqual(actual, expected);
    });
}());