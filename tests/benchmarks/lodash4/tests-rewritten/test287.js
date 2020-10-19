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
                    'y': __num_top__
                }
            ], others = [
                {
                    'x': __num_top__,
                    'y': __num_top__
                },
                {
                    'x': __num_top__,
                    'y': __num_top__
                }
            ], actual = _.xorWith(objects, others, lodashStable.isEqual);
        assert.deepEqual(actual, [
            objects[__num_top__],
            others[__num_top__]
        ]);
    });
}());