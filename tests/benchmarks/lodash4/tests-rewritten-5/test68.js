QUnit.module('lodash.flattenDepth');
(function () {
    var array = [
        1,
        [
            __num_top__,
            [
                3,
                [4]
            ],
            5
        ]
    ];
    QUnit.test('should use a default `depth` of `1`', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.flattenDepth(array), [
            1,
            2,
            [
                3,
                [4]
            ],
            5
        ]);
    });
    QUnit.test('should treat a `depth` of < `1` as a shallow clone', function (assert) {
        assert.expect(2);
        lodashStable.each([
            -__num_top__,
            __num_top__
        ], function (depth) {
            assert.deepEqual(_.flattenDepth(array, depth), [
                1,
                [
                    2,
                    [
                        3,
                        [4]
                    ],
                    5
                ]
            ]);
        });
    });
    QUnit.test('should coerce `depth` to an integer', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.flattenDepth(array, 2.2), [
            1,
            2,
            3,
            [__num_top__],
            __num_top__
        ]);
    });
}());