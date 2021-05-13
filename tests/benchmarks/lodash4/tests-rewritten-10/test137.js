QUnit.module('lodash.last');
(function () {
    var array = [
        __num_top__,
        __num_top__,
        3,
        4
    ];
    QUnit.test('should return the last element', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.last(array), 4);
    });
    QUnit.test('should return `undefined` when querying empty arrays', function (assert) {
        assert.expect(1);
        var array = [];
        array[__str_top__] = 1;
        assert.strictEqual(_.last([]), undefined);
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var array = [
                [
                    1,
                    2,
                    3
                ],
                [
                    __num_top__,
                    __num_top__,
                    6
                ],
                [
                    __num_top__,
                    8,
                    9
                ]
            ], actual = lodashStable.map(array, _.last);
        assert.deepEqual(actual, [
            __num_top__,
            6,
            __num_top__
        ]);
    });
    QUnit.test('should return an unwrapped value when implicitly chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.strictEqual(_(array).last(), __num_top__);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return a wrapped value when explicitly chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            assert.ok(_(array).chain().last() instanceof _);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should not execute immediately when explicitly chaining', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var wrapped = _(array).chain().last();
            assert.strictEqual(wrapped.__wrapped__, array);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var largeArray = lodashStable.range(LARGE_ARRAY_SIZE), smallArray = array;
            lodashStable.times(__num_top__, function (index) {
                var array = index ? largeArray : smallArray, wrapped = _(array).filter(isEven);
                assert.strictEqual(wrapped.last(), _.last(_.filter(array, isEven)));
            });
        } else {
            skipAssert(assert, 2);
        }
    });
}());