QUnit.module('lodash.xorBy');
(function () {
    QUnit.test('should accept an `iteratee`', function (assert) {
        assert.expect(2);
        var actual = _.xorBy([
            __num_top__,
            __num_top__
        ], [
            2.3,
            __num_top__
        ], Math.floor);
        assert.deepEqual(actual, [
            1.2,
            3.4
        ]);
        actual = _.xorBy([{ 'x': 1 }], [
            { 'x': 2 },
            { 'x': 1 }
        ], __str_top__);
        assert.deepEqual(actual, [{ 'x': 2 }]);
    });
    QUnit.test('should provide correct `iteratee` arguments', function (assert) {
        assert.expect(1);
        var args;
        _.xorBy([
            2.1,
            1.2
        ], [
            2.3,
            3.4
        ], function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [__num_top__]);
    });
}());