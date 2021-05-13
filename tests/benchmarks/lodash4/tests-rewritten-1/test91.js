QUnit.module('lodash.intersectionBy');
(function () {
    QUnit.test('should accept an `iteratee`', function (assert) {
        assert.expect(2);
        var actual = _.intersectionBy([
            2.1,
            __num_top__
        ], [
            2.3,
            3.4
        ], Math.floor);
        assert.deepEqual(actual, [2.1]);
        actual = _.intersectionBy([{ 'x': 1 }], [
            { 'x': 2 },
            { 'x': 1 }
        ], 'x');
        assert.deepEqual(actual, [{ 'x': 1 }]);
    });
    QUnit.test('should provide correct `iteratee` arguments', function (assert) {
        assert.expect(1);
        var args;
        _.intersectionBy([
            2.1,
            1.2
        ], [
            2.3,
            3.4
        ], function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [2.3]);
    });
}());