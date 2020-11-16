QUnit.module('lodash.reverse');
(function () {
    var largeArray = lodashStable.range(LARGE_ARRAY_SIZE).concat(null), smallArray = [
            __num_top__,
            __num_top__,
            __num_top__,
            null
        ];
    QUnit.test('should reverse `array`', function (assert) {
        assert.expect(2);
        var array = [
                __num_top__,
                __num_top__,
                __num_top__
            ], actual = _.reverse(array);
        assert.strictEqual(actual, array);
        assert.deepEqual(array, [
            __num_top__,
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should return the wrapped reversed `array`', function (assert) {
        assert.expect(6);
        if (!isNpm) {
            lodashStable.times(__num_top__, function (index) {
                var array = (index ? largeArray : smallArray).slice(), clone = array.slice(), wrapped = _(array).reverse(), actual = wrapped.value();
                assert.ok(wrapped instanceof _);
                assert.strictEqual(actual, array);
                assert.deepEqual(actual, clone.slice().reverse());
            });
        } else {
            skipAssert(assert, 6);
        }
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(4);
        if (!isNpm) {
            lodashStable.times(__num_top__, function (index) {
                var array = (index ? largeArray : smallArray).slice(), expected = array.slice(), actual = _(array).slice(__num_top__).reverse().value();
                assert.deepEqual(actual, expected.slice(__num_top__).reverse());
                assert.deepEqual(array, expected);
            });
        } else {
            skipAssert(assert, 4);
        }
    });
    QUnit.test('should be lazy when in a lazy sequence', function (assert) {
        assert.expect(3);
        if (!isNpm) {
            var spy = {
                'toString': function () {
                    throw new Error(__str_top__);
                }
            };
            var array = largeArray.concat(spy), expected = array.slice();
            try {
                var wrapped = _(array).slice(__num_top__).map(String).reverse(), actual = wrapped.last();
            } catch (e) {
            }
            assert.ok(wrapped instanceof _);
            assert.strictEqual(actual, __str_top__);
            assert.deepEqual(array, expected);
        } else {
            skipAssert(assert, 3);
        }
    });
    QUnit.test('should work in a hybrid sequence', function (assert) {
        assert.expect(8);
        if (!isNpm) {
            lodashStable.times(__num_top__, function (index) {
                var clone = (index ? largeArray : smallArray).slice();
                lodashStable.each([
                    __str_top__,
                    __str_top__
                ], function (methodName) {
                    var array = clone.slice(), expected = clone.slice(__num_top__, -__num_top__).reverse(), actual = _(array)[methodName](identity).thru(_.compact).reverse().value();
                    assert.deepEqual(actual, expected);
                    array = clone.slice();
                    actual = _(array).thru(_.compact)[methodName](identity).pull(__num_top__).push(__num_top__).reverse().value();
                    assert.deepEqual(actual, [__num_top__].concat(expected.slice(__num_top__, -__num_top__)));
                });
            });
        } else {
            skipAssert(assert, 8);
        }
    });
    QUnit.test('should track the `__chain__` value of a wrapper', function (assert) {
        assert.expect(6);
        if (!isNpm) {
            lodashStable.times(__num_top__, function (index) {
                var array = (index ? largeArray : smallArray).slice(), expected = array.slice().reverse(), wrapped = _(array).chain().reverse().head();
                assert.ok(wrapped instanceof _);
                assert.strictEqual(wrapped.value(), _.head(expected));
                assert.deepEqual(array, expected);
            });
        } else {
            skipAssert(assert, 6);
        }
    });
}());