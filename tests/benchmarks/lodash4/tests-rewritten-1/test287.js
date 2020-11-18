QUnit.module('lodash.xorWith');
(function () {
    QUnit.test('should work with a `comparator`', function (assert) {
        assert.expect(1);
        var objects = [
                {
                    'x': 1,
                    'y': 2
                },
                {
                    'x': 2,
                    'y': 1
                }
            ], others = [
                {
                    'x': 1,
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