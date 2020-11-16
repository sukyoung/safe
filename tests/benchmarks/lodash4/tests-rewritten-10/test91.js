QUnit.module('lodash.intersectionBy');
(function () {
    QUnit.test('should accept an `iteratee`', function (assert) {
        assert.expect(2);
        var actual = _.intersectionBy([
            2.1,
            __num_top__
        ], [
            __num_top__,
            __num_top__
        ], Math.floor);
        assert.deepEqual(actual, [2.1]);
        actual = _.intersectionBy([{ 'x': __num_top__ }], [
            { 'x': __num_top__ },
            { 'x': __num_top__ }
        ], __str_top__);
        assert.deepEqual(actual, [{ 'x': 1 }]);
    });
    QUnit.test('should provide correct `iteratee` arguments', function (assert) {
        assert.expect(1);
        var args;
        _.intersectionBy([
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