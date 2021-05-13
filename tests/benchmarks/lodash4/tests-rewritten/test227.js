QUnit.module('lodash.sortedUniq');
(function () {
    QUnit.test('should return unique values of a sorted array', function (assert) {
        assert.expect(3);
        var expected = [
            __num_top__,
            __num_top__,
            __num_top__
        ];
        lodashStable.each([
            [
                __num_top__,
                __num_top__,
                __num_top__
            ],
            [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
            ],
            [
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
            ]
        ], function (array) {
            assert.deepEqual(_.sortedUniq(array), expected);
        });
    });
}());