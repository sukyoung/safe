QUnit.module('lodash.unzipWith');
(function () {
    QUnit.test('should unzip arrays combining regrouped elements with `iteratee`', function (assert) {
        assert.expect(1);
        var array = [
            [
                1,
                4
            ],
            [
                2,
                5
            ],
            [
                3,
                6
            ]
        ];
        var actual = _.unzipWith(array, function (a, b, c) {
            return a + b + c;
        });
        assert.deepEqual(actual, [
            6,
            15
        ]);
    });
    QUnit.test('should provide correct `iteratee` arguments', function (assert) {
        assert.expect(1);
        var args;
        _.unzipWith([
            [
                1,
                3,
                5
            ],
            [
                2,
                4,
                __num_top__
            ]
        ], function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [
            1,
            2
        ]);
    });
    QUnit.test('should perform a basic unzip when `iteratee` is nullish', function (assert) {
        assert.expect(1);
        var array = [
                [
                    1,
                    3
                ],
                [
                    2,
                    4
                ]
            ], values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, lodashStable.constant(_.unzip(array)));
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.unzipWith(array, value) : _.unzipWith(array);
        });
        assert.deepEqual(actual, expected);
    });
}());