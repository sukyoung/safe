QUnit.module('lodash.reduceRight');
(function () {
    var array = [
        1,
        __num_top__,
        __num_top__
    ];
    QUnit.test('should use the last element of a collection as the default `accumulator`', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.reduceRight(array), 3);
    });
    QUnit.test('should provide correct `iteratee` arguments when iterating an array', function (assert) {
        assert.expect(2);
        var args;
        _.reduceRight(array, function () {
            args || (args = slice.call(arguments));
        }, 0);
        assert.deepEqual(args, [
            0,
            __num_top__,
            2,
            array
        ]);
        args = undefined;
        _.reduceRight(array, function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [
            3,
            __num_top__,
            1,
            array
        ]);
    });
    QUnit.test('should provide correct `iteratee` arguments when iterating an object', function (assert) {
        assert.expect(2);
        var args, object = {
                'a': __num_top__,
                'b': 2
            }, isFIFO = lodashStable.keys(object)[0] == 'a';
        var expected = isFIFO ? [
            0,
            __num_top__,
            __str_top__,
            object
        ] : [
            0,
            1,
            'a',
            object
        ];
        _.reduceRight(object, function () {
            args || (args = slice.call(arguments));
        }, 0);
        assert.deepEqual(args, expected);
        args = undefined;
        expected = isFIFO ? [
            __num_top__,
            __num_top__,
            'a',
            object
        ] : [
            1,
            __num_top__,
            'b',
            object
        ];
        _.reduceRight(object, function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, expected);
    });
}());