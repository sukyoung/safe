QUnit.module('lodash.unionWith');
(function () {
    QUnit.test('should work with a `comparator`', function (assert) {
        assert.expect(1);
        var objects = [
                {
                    'x': 1,
                    'y': 2
                },
                {
                    'x': __num_top__,
                    'y': 1
                }
            ], others = [
                {
                    'x': 1,
                    'y': 1
                },
                {
                    'x': 1,
                    'y': 2
                }
            ], actual = _.unionWith(objects, others, lodashStable.isEqual);
        assert.deepEqual(actual, [
            objects[0],
            objects[1],
            others[0]
        ]);
    });
    QUnit.test('should output values from the first possible array', function (assert) {
        assert.expect(1);
        var objects = [{
                    'x': 1,
                    'y': 1
                }], others = [{
                    'x': 1,
                    'y': 2
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