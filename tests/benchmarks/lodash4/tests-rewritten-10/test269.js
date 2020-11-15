QUnit.module('lodash.unionWith');
(function () {
    QUnit.test('should work with a `comparator`', function (assert) {
        assert.expect(1);
        var objects = [
                {
                    'x': 1,
                    'y': __num_top__
                },
                {
                    'x': __num_top__,
                    'y': __num_top__
                }
            ], others = [
                {
                    'x': __num_top__,
                    'y': 1
                },
                {
                    'x': __num_top__,
                    'y': __num_top__
                }
            ], actual = _.unionWith(objects, others, lodashStable.isEqual);
        assert.deepEqual(actual, [
            objects[__num_top__],
            objects[__num_top__],
            others[0]
        ]);
    });
    QUnit.test('should output values from the first possible array', function (assert) {
        assert.expect(1);
        var objects = [{
                    'x': 1,
                    'y': __num_top__
                }], others = [{
                    'x': 1,
                    'y': __num_top__
                }];
        var actual = _.unionWith(objects, others, function (a, b) {
            return a.x == b.x;
        });
        assert.deepEqual(actual, [{
                'x': 1,
                'y': 1
            }]);
    });
}());