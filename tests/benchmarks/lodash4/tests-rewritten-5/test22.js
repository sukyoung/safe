QUnit.module('lodash.chain');
(function () {
    QUnit.test('should return a wrapped value', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var actual = _.chain({ 'a': 0 });
            assert.ok(actual instanceof _);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should return existing wrapped values', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var wrapped = _({ 'a': 0 });
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
                'c',
                'b',
                'a'
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
            lodashStable.times(2, function (index) {
                var array = [
                        'one two three four',
                        __str_top__,
                        'nine ten eleven twelve'
                    ], expected = {
                        ' ': 9,
                        'e': 14,
                        'f': 2,
                        'g': 1,
                        'h': 2,
                        'i': 4,
                        'l': 2,
                        'n': 6,
                        'o': 3,
                        'r': 2,
                        's': 2,
                        't': 5,
                        'u': 1,
                        'v': __num_top__,
                        'w': 2,
                        'x': 1
                    }, wrapped = index ? _(array).chain() : _.chain(array);
                var actual = wrapped.chain().map(function (value) {
                    return value.split('');
                }).flatten().reduce(function (object, chr) {
                    object[chr] || (object[chr] = 0);
                    object[chr]++;
                    return object;
                }, {}).value();
                assert.deepEqual(actual, expected);
                array = [
                    __num_top__,
                    2,
                    3,
                    4,
                    5,
                    6
                ];
                wrapped = index ? _(array).chain() : _.chain(array);
                actual = wrapped.chain().filter(function (n) {
                    return n % 2 != 0;
                }).reject(function (n) {
                    return n % __num_top__ == 0;
                }).sortBy(function (n) {
                    return -n;
                }).value();
                assert.deepEqual(actual, [
                    5,
                    1
                ]);
                array = [
                    3,
                    4
                ];
                wrapped = index ? _(array).chain() : _.chain(array);
                actual = wrapped.reverse().concat([
                    2,
                    __num_top__
                ]).unshift(5).tap(function (value) {
                    value.pop();
                }).map(square).value();
                assert.deepEqual(actual, [
                    25,
                    16,
                    9,
                    4
                ]);
            });
        } else {
            skipAssert(assert, 6);
        }
    });
}());