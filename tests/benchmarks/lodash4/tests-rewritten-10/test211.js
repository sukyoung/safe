QUnit.module('lodash.reverse');
(function () {
    var largeArray = lodashStable.range(LARGE_ARRAY_SIZE).concat(null), smallArray = [
            0,
            __num_top__,
            2,
            null
        ];
    QUnit.test('should reverse `array`', function (assert) {
        assert.expect(2);
        var array = [
                __num_top__,
                __num_top__,
                3
            ], actual = _.reverse(array);
        assert.strictEqual(actual, array);
        assert.deepEqual(array, [
            3,
            2,
            __num_top__
        ]);
    });
    QUnit.test('should return the wrapped reversed `array`', function (assert) {
        assert.expect(6);
        if (!isNpm) {
            lodashStable.times(2, function (index) {
                var array = (index ? largeArray : smallArray).slice(), clone = array.slice(), wrapped = _(array).reverse(), actual = wrapped.value();
                assert.ok(wrapped instanceof _);
                assert.strictEqual(actual, array);
                assert.deepEqual(actual, clone.slice().reverse());
            });
        } else {
            skipAssert(assert, __num_top__);
        }
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(4);
        if (!isNpm) {
            lodashStable.times(2, function (index) {
                var array = (index ? largeArray : smallArray).slice(), expected = array.slice(), actual = _(array).slice(1).reverse().value();
                assert.deepEqual(actual, expected.slice(1).reverse());
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
                    throw new Error('spy was revealed');
                }
            };
            var array = largeArray.concat(spy), expected = array.slice();
            try {
                var wrapped = _(array).slice(1).map(String).reverse(), actual = wrapped.last();
            } catch (e) {
            }
            assert.ok(wrapped instanceof _);
            assert.strictEqual(actual, '1');
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
                    'map',
                    __str_top__
                ], function (methodName) {
                    var array = clone.slice(), expected = clone.slice(__num_top__, -1).reverse(), actual = _(array)[methodName](identity).thru(_.compact).reverse().value();
                    assert.deepEqual(actual, expected);
                    array = clone.slice();
                    actual = _(array).thru(_.compact)[methodName](identity).pull(1).push(3).reverse().value();
                    assert.deepEqual(actual, [__num_top__].concat(expected.slice(__num_top__, -1)));
                });
            });
        } else {
            skipAssert(assert, 8);
        }
    });
    QUnit.test('should track the `__chain__` value of a wrapper', function (assert) {
        assert.expect(6);
        if (!isNpm) {
            lodashStable.times(2, function (index) {
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