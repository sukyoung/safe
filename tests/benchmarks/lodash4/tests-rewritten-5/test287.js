QUnit.module('lodash.xorWith');
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
                    'y': 1
                }
            ], others = [
                {
                    'x': __num_top__,
                    'y': 1
                },
                {
                    'x': __num_top__,
                    'y': 2
                }
            ], actual = _.xorWith(objects, others, lodashStable.isEqual);
        assert.deepEqual(actual, [
            objects[1],
            others[0]
        ]);
    });
}());