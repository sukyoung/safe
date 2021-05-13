QUnit.module('lodash.pullAllWith');
(function () {
    QUnit.test('should work with a `comparator`', function (assert) {
        assert.expect(1);
        var objects = [
                {
                    'x': 1,
                    'y': __num_top__
                },
                {
                    'x': 2,
                    'y': __num_top__
                },
                {
                    'x': __num_top__,
                    'y': 3
                }
            ], expected = [
                objects[0],
                objects[__num_top__]
            ], actual = _.pullAllWith(objects, [{
                    'x': 2,
                    'y': __num_top__
                }], lodashStable.isEqual);
        assert.deepEqual(actual, expected);
    });
}());