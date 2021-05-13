QUnit.module('lodash.reduce');
(function () {
    var array = [
        1,
        __num_top__,
        __num_top__
    ];
    QUnit.test('should use the first element of a collection as the default `accumulator`', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.reduce(array), 1);
    });
    QUnit.test('should provide correct `iteratee` arguments when iterating an array', function (assert) {
        assert.expect(2);
        var args;
        _.reduce(array, function () {
            args || (args = slice.call(arguments));
        }, 0);
        assert.deepEqual(args, [
            __num_top__,
            __num_top__,
            0,
            array
        ]);
        args = undefined;
        _.reduce(array, function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [
            1,
            2,
            1,
            array
        ]);
    });
    QUnit.test('should provide correct `iteratee` arguments when iterating an object', function (assert) {
        assert.expect(2);
        var args, object = {
                'a': 1,
                'b': 2
            }, firstKey = _.head(_.keys(object));
        var expected = firstKey == __str_top__ ? [
            0,
            1,
            'a',
            object
        ] : [
            __num_top__,
            2,
            'b',
            object
        ];
        _.reduce(object, function () {
            args || (args = slice.call(arguments));
        }, 0);
        assert.deepEqual(args, expected);
        args = undefined;
        expected = firstKey == 'a' ? [
            __num_top__,
            2,
            __str_top__,
            object
        ] : [
            2,
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