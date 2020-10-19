QUnit.module('lodash.xorBy');
(function () {
    QUnit.test('should accept an `iteratee`', function (assert) {
        assert.expect(2);
        var actual = _.xorBy([
            __num_top__,
            __num_top__
        ], [
            __num_top__,
            __num_top__
        ], Math.floor);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__
        ]);
        actual = _.xorBy([{ 'x': __num_top__ }], [
            { 'x': __num_top__ },
            { 'x': __num_top__ }
        ], __str_top__);
        assert.deepEqual(actual, [{ 'x': __num_top__ }]);
    });
    QUnit.test('should provide correct `iteratee` arguments', function (assert) {
        assert.expect(1);
        var args;
        _.xorBy([
            __num_top__,
            __num_top__
        ], [
            __num_top__,
            __num_top__
        ], function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [__num_top__]);
    });
}());