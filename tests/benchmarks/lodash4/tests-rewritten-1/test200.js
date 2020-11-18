QUnit.module('lodash.reduce');
(function () {
    var array = [
        1,
        2,
        3
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
            0,
            1,
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
        var expected = firstKey == 'a' ? [
            0,
            1,
            'a',
            object
        ] : [
            0,
            2,
            __str_top__,
            object
        ];
        _.reduce(object, function () {
            args || (args = slice.call(arguments));
        }, 0);
        assert.deepEqual(args, expected);
        args = undefined;
        expected = firstKey == 'a' ? [
            1,
            2,
            'b',
            object
        ] : [
            2,
            1,
            'a',
            object
        ];
        _.reduce(object, function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, expected);
    });
}());