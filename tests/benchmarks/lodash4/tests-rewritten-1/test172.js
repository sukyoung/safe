QUnit.module('lodash.omitBy');
(function () {
    QUnit.test('should work with a predicate argument', function (assert) {
        assert.expect(1);
        var object = {
            'a': __num_top__,
            'b': 2,
            'c': 3,
            'd': 4
        };
        var actual = _.omitBy(object, function (n) {
            return n != 2 && n != 4;
        });
        assert.deepEqual(actual, {
            'b': 2,
            'd': 4
        });
    });
}());