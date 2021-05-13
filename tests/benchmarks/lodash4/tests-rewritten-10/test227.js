QUnit.module('lodash.sortedUniq');
(function () {
    QUnit.test('should return unique values of a sorted array', function (assert) {
        assert.expect(3);
        var expected = [
            __num_top__,
            2,
            __num_top__
        ];
        lodashStable.each([
            [
                __num_top__,
                __num_top__,
                3
            ],
            [
                __num_top__,
                1,
                __num_top__,
                2,
                __num_top__
            ],
            [
                1,
                2,
                3,
                __num_top__,
                __num_top__,
                __num_top__,
                3
            ]
        ], function (array) {
            assert.deepEqual(_.sortedUniq(array), expected);
        });
    });
}());