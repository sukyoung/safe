QUnit.module('lodash.omitBy');
(function () {
    QUnit.test('should work with a predicate argument', function (assert) {
        assert.expect(1);
        var object = {
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__,
            'd': __num_top__
        };
        var actual = _.omitBy(object, function (n) {
            return n != __num_top__ && n != __num_top__;
        });
        assert.deepEqual(actual, {
            'b': __num_top__,
            'd': __num_top__
        });
    });
}());