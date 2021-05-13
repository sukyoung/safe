QUnit.module('lodash.unionBy');
(function () {
    QUnit.test('should accept an `iteratee`', function (assert) {
        assert.expect(2);
        var actual = _.unionBy([__num_top__], [
            1.2,
            __num_top__
        ], Math.floor);
        assert.deepEqual(actual, [
            2.1,
            1.2
        ]);
        actual = _.unionBy([{ 'x': 1 }], [
            { 'x': 2 },
            { 'x': 1 }
        ], 'x');
        assert.deepEqual(actual, [
            { 'x': __num_top__ },
            { 'x': 2 }
        ]);
    });
    QUnit.test('should provide correct `iteratee` arguments', function (assert) {
        assert.expect(1);
        var args;
        _.unionBy([2.1], [
            1.2,
            2.3
        ], function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [2.1]);
    });
    QUnit.test('should output values from the first possible array', function (assert) {
        assert.expect(1);
        var actual = _.unionBy([{
                'x': __num_top__,
                'y': 1
            }], [{
                'x': 1,
                'y': __num_top__
            }], 'x');
        assert.deepEqual(actual, [{
                'x': 1,
                'y': 1
            }]);
    });
}());