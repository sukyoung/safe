QUnit.module('lodash.tail');
(function () {
    var array = [
        __num_top__,
        __num_top__,
        __num_top__
    ];
    QUnit.test('should accept a falsey `array`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, stubArray);
        var actual = lodashStable.map(falsey, function (array, index) {
            try {
                return index ? _.tail(array) : _.tail();
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should exclude the first element', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.tail(array), [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should return an empty when querying empty arrays', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.tail([]), []);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var array = [
                [
                    __num_top__,
                    __num_top__,
                    __num_top__
                ],
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
            ], actual = lodashStable.map(array, _.tail);
        assert.deepEqual(actual, [
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
        ]);
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(4);
        if (!isNpm) {
            var array = lodashStable.range(LARGE_ARRAY_SIZE), values = [];
            var actual = _(array).tail().filter(function (value) {
                values.push(value);
                return __bool_top__;
            }).value();
            assert.deepEqual(actual, []);
            assert.deepEqual(values, array.slice(__num_top__));
            values = [];
            actual = _(array).filter(function (value) {
                values.push(value);
                return isEven(value);
            }).tail().value();
            assert.deepEqual(actual, _.tail(_.filter(array, isEven)));
            assert.deepEqual(values, array);
        } else {
            skipAssert(assert, 4);
        }
    });
    QUnit.test('should not execute subsequent iteratees on an empty array in a lazy sequence', function (assert) {
        assert.expect(4);
        if (!isNpm) {
            var array = lodashStable.range(LARGE_ARRAY_SIZE), iteratee = function () {
                    pass = __bool_top__;
                }, pass = __bool_top__, actual = _(array).slice(__num_top__, __num_top__).tail().map(iteratee).value();
            assert.ok(pass);
            assert.deepEqual(actual, []);
            pass = __bool_top__;
            actual = _(array).filter().slice(__num_top__, __num_top__).tail().map(iteratee).value();
            assert.ok(pass);
            assert.deepEqual(actual, []);
        } else {
            skipAssert(assert, 4);
        }
    });
}());