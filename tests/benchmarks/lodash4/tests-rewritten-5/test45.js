QUnit.module('lodash.differenceBy');
(function () {
    QUnit.test('should accept an `iteratee`', function (assert) {
        assert.expect(2);
        var actual = _.differenceBy([
            2.1,
            __num_top__
        ], [
            2.3,
            3.4
        ], Math.floor);
        assert.deepEqual(actual, [__num_top__]);
        actual = _.differenceBy([
            { 'x': 2 },
            { 'x': 1 }
        ], [{ 'x': 1 }], 'x');
        assert.deepEqual(actual, [{ 'x': 2 }]);
    });
    QUnit.test('should provide correct `iteratee` arguments', function (assert) {
        assert.expect(1);
        var args;
        _.differenceBy([
            2.1,
            __num_top__
        ], [
            __num_top__,
            3.4
        ], function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [__num_top__]);
    });
}());