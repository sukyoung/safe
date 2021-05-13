QUnit.module('lodash.head');
(function () {
    var array = [
        __num_top__,
        __num_top__,
        __num_top__,
        __num_top__
    ];
    QUnit.test('should return the first element', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.head(array), __num_top__);
    });
    QUnit.test('should return `undefined` when querying empty arrays', function (assert) {
        assert.expect(1);
        arrayProto[__num_top__] = __num_top__;
        assert.strictEqual(_.head([]), undefined);
        arrayProto.length = __num_top__;
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
            ], actual = lodashStable.map(array, _.head);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should be aliased', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.first, _.head);
    });
    QUnit.test('should return an unwrapped value when implicitly chaining', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var wrapped = _(array);
            assert.strictEqual(wrapped.head(), __num_top__);
            assert.strictEqual(wrapped.first(), __num_top__);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should return a wrapped value when explicitly chaining', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var wrapped = _(array).chain();
            assert.ok(wrapped.head() instanceof _);
            assert.ok(wrapped.first() instanceof _);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should not execute immediately when explicitly chaining', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var wrapped = _(array).chain();
            assert.strictEqual(wrapped.head().__wrapped__, array);
            assert.strictEqual(wrapped.first().__wrapped__, array);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(4);
        if (!isNpm) {
            var largeArray = lodashStable.range(LARGE_ARRAY_SIZE), smallArray = array;
            lodashStable.each([
                __str_top__,
                __str_top__
            ], function (methodName) {
                lodashStable.times(__num_top__, function (index) {
                    var array = index ? largeArray : smallArray, actual = _(array).filter(isEven)[methodName]();
                    assert.strictEqual(actual, _[methodName](_.filter(array, isEven)));
                });
            });
        } else {
            skipAssert(assert, 4);
        }
    });
}());