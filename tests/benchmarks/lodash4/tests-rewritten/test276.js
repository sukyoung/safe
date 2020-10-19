QUnit.module('lodash.unzipWith');
(function () {
    QUnit.test('should unzip arrays combining regrouped elements with `iteratee`', function (assert) {
        assert.expect(1);
        var array = [
            [
                __num_top__,
                __num_top__
            ],
            [
                __num_top__,
                __num_top__
            ],
            [
                __num_top__,
                __num_top__
            ]
        ];
        var actual = _.unzipWith(array, function (a, b, c) {
            return a + b + c;
        });
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should provide correct `iteratee` arguments', function (assert) {
        assert.expect(1);
        var args;
        _.unzipWith([
            [
                __num_top__,
                __num_top__,
                __num_top__
            ],
            [
                __num_top__,
                __num_top__,
                __num_top__
            ]
        ], function () {
            args || (args = slice.call(arguments));
        });
        assert.deepEqual(args, [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should perform a basic unzip when `iteratee` is nullish', function (assert) {
        assert.expect(1);
        var array = [
                [
                    __num_top__,
                    __num_top__
                ],
                [
                    __num_top__,
                    __num_top__
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