QUnit.module('lodash.reduce');
(function () {
    var array = [
        __num_top__,
        __num_top__,
        __num_top__
    ];
    QUnit.test('should use the first element of a collection as the default `accumulator`', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.reduce(array), __num_top__);
    });
    QUnit.test('should provide correct `iteratee` arguments when iterating an array', function (assert) {
        assert.expect(2);
        var args;
        _.reduce(array, function () {
            args || (args = slice.call(arguments));
        }, __num_top__);
        assert.deepEqual(args, [
            __num_top__,
            __num_top__,
            __num_top__,
            array
        ]);
        args = undefined;
        _.reduce(array, function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [
            __num_top__,
            __num_top__,
            __num_top__,
            array
        ]);
    });
    QUnit.test('should provide correct `iteratee` arguments when iterating an object', function (assert) {
        assert.expect(2);
        var args, object = {
                'a': __num_top__,
                'b': __num_top__
            }, firstKey = _.head(_.keys(object));
        var expected = firstKey == __str_top__ ? [
            __num_top__,
            __num_top__,
            __str_top__,
            object
        ] : [
            __num_top__,
            __num_top__,
            __str_top__,
            object
        ];
        _.reduce(object, function () {
            args || (args = slice.call(arguments));
        }, __num_top__);
        assert.deepEqual(args, expected);
        args = undefined;
        expected = firstKey == __str_top__ ? [
            __num_top__,
            __num_top__,
            __str_top__,
            object
        ] : [
            __num_top__,
            __num_top__,
            __str_top__,
            object
        ];
        _.reduce(object, function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, expected);
    });
}());