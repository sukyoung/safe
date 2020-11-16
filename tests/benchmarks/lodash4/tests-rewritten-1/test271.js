QUnit.module('lodash.uniq');
(function () {
    QUnit.test('should perform an unsorted uniq when used as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var array = [
                [
                    __num_top__,
                    1,
                    2
                ],
                [
                    1,
                    2,
                    1
                ]
            ], actual = lodashStable.map(array, lodashStable.uniq);
        assert.deepEqual(actual, [
            [
                2,
                1
            ],
            [
                1,
                2
            ]
        ]);
    });
}());