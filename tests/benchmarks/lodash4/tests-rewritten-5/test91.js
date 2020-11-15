QUnit.module('lodash.intersectionBy');
(function () {
    QUnit.test('should accept an `iteratee`', function (assert) {
        assert.expect(2);
        var actual = _.intersectionBy([
            __num_top__,
            1.2
        ], [
            2.3,
            3.4
        ], Math.floor);
        assert.deepEqual(actual, [__num_top__]);
        actual = _.intersectionBy([{ 'x': 1 }], [
            { 'x': 2 },
            { 'x': 1 }
        ], 'x');
        assert.deepEqual(actual, [{ 'x': __num_top__ }]);
    });
    QUnit.test('should provide correct `iteratee` arguments', function (assert) {
        assert.expect(1);
        var args;
        _.intersectionBy([
            __num_top__,
            1.2
        ], [
            2.3,
            __num_top__
        ], function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [2.3]);
    });
}());