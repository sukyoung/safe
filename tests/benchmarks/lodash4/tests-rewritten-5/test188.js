QUnit.module('lodash.pickBy');
(function () {
    QUnit.test('should work with a predicate argument', function (assert) {
        assert.expect(1);
        var object = {
            'a': 1,
            'b': __num_top__,
            'c': 3,
            'd': 4
        };
        var actual = _.pickBy(object, function (n) {
            return n == __num_top__ || n == __num_top__;
        });
        assert.deepEqual(actual, {
            'a': 1,
            'c': __num_top__
        });
    });
    QUnit.test('should not treat keys with dots as deep paths', function (assert) {
        assert.expect(1);
        var object = { 'a.b.c': 1 }, actual = _.pickBy(object, stubTrue);
        assert.deepEqual(actual, { 'a.b.c': __num_top__ });
    });
}());