QUnit.module('lodash.chain');
(function () {
    QUnit.test('should return a wrapped value', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var actual = _.chain({ 'a': __num_top__ });
            assert.ok(actual instanceof _);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return existing wrapped values', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var wrapped = _({ 'a': __num_top__ });
            assert.strictEqual(_.chain(wrapped), wrapped);
            assert.strictEqual(wrapped.chain(), wrapped);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should enable chaining for methods that return unwrapped values', function (assert) {
        assert.expect(6);
        if (!isNpm) {
            var array = [
                __str_top__,
                __str_top__,
                __str_top__
            ];
            assert.ok(_.chain(array).head() instanceof _);
            assert.ok(_(array).chain().head() instanceof _);
            assert.ok(_.chain(array).isArray() instanceof _);
            assert.ok(_(array).chain().isArray() instanceof _);
            assert.ok(_.chain(array).sortBy().head() instanceof _);
            assert.ok(_(array).chain().sortBy().head() instanceof _);
        } else {
            skipAssert(assert, 6);
        }
    });
    QUnit.test('should chain multiple methods', function (assert) {
        assert.expect(6);
        if (!isNpm) {
            lodashStable.times(__num_top__, function (index) {
                var array = [
                        __str_top__,
                        __str_top__,
                        __str_top__
                    ], expected = {
                        ' ': __num_top__,
                        'e': __num_top__,
                        'f': __num_top__,
                        'g': __num_top__,
                        'h': __num_top__,
                        'i': __num_top__,
                        'l': __num_top__,
                        'n': __num_top__,
                        'o': __num_top__,
                        'r': __num_top__,
                        's': __num_top__,
                        't': __num_top__,
                        'u': __num_top__,
                        'v': __num_top__,
                        'w': __num_top__,
                        'x': __num_top__
                    }, wrapped = index ? _(array).chain() : _.chain(array);
                var actual = wrapped.chain().map(function (value) {
                    return value.split(__str_top__);
                }).flatten().reduce(function (object, chr) {
                    object[chr] || (object[chr] = __num_top__);
                    object[chr]++;
                    return object;
                }, {}).value();
                assert.deepEqual(actual, expected);
                array = [
                    __num_top__,
                    __num_top__,
                    __num_top__,
                    __num_top__,
                    __num_top__,
                    __num_top__
                ];
                wrapped = index ? _(array).chain() : _.chain(array);
                actual = wrapped.chain().filter(function (n) {
                    return n % __num_top__ != __num_top__;
                }).reject(function (n) {
                    return n % __num_top__ == __num_top__;
                }).sortBy(function (n) {
                    return -n;
                }).value();
                assert.deepEqual(actual, [
                    __num_top__,
                    __num_top__
                ]);
                array = [
                    __num_top__,
                    __num_top__
                ];
                wrapped = index ? _(array).chain() : _.chain(array);
                actual = wrapped.reverse().concat([
                    __num_top__,
                    __num_top__
                ]).unshift(__num_top__).tap(function (value) {
                    value.pop();
                }).map(square).value();
                assert.deepEqual(actual, [
                    __num_top__,
                    __num_top__,
                    __num_top__,
                    __num_top__
                ]);
            });
        } else {
            skipAssert(assert, 6);
        }
    });
}());