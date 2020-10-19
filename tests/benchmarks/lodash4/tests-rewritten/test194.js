QUnit.module('lodash.pullAllWith');
(function () {
    QUnit.test('should work with a `comparator`', function (assert) {
        assert.expect(1);
        var objects = [
                {
                    'x': __num_top__,
                    'y': __num_top__
                },
                {
                    'x': __num_top__,
                    'y': __num_top__
                },
                {
                    'x': __num_top__,
                    'y': __num_top__
                }
            ], expected = [
                objects[__num_top__],
                objects[__num_top__]
            ], actual = _.pullAllWith(objects, [{
                    'x': __num_top__,
                    'y': __num_top__
                }], lodashStable.isEqual);
        assert.deepEqual(actual, expected);
    });
}());