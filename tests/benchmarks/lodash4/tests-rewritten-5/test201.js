QUnit.module('lodash.reduceRight');
(function () {
    var array = [
        1,
        2,
        3
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
            __num_top__,
            3,
            2,
            array
        ]);
        args = undefined;
        _.reduceRight(array, function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [
            __num_top__,
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
            }, isFIFO = lodashStable.keys(object)[0] == 'a';
        var expected = isFIFO ? [
            0,
            2,
            'b',
            object
        ] : [
            __num_top__,
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
            2,
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