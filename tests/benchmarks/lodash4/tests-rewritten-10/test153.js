QUnit.module('lodash.meanBy');
(function () {
    var objects = [
        { 'a': __num_top__ },
        { 'a': __num_top__ },
        { 'a': __num_top__ }
    ];
    QUnit.test('should work with an `iteratee`', function (assert) {
        assert.expect(1);
        var actual = _.meanBy(objects, function (object) {
            return object.a;
        });
        assert.deepEqual(actual, __num_top__);
    });
    QUnit.test('should provide correct `iteratee` arguments', function (assert) {
        assert.expect(1);
        var args;
        _.meanBy(objects, function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [{ 'a': 2 }]);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(2);
        var arrays = [
            [__num_top__],
            [__num_top__],
            [1]
        ];
        assert.strictEqual(_.meanBy(arrays, __num_top__), __num_top__);
        assert.strictEqual(_.meanBy(objects, __str_top__), __num_top__);
    });
}());