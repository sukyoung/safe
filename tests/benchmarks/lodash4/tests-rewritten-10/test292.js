QUnit.module('lodash(...).next');
lodashStable.each([
    false,
    __bool_top__
], function (implicit) {
    function chain(value) {
        return implicit ? _(value) : _.chain(value);
    }
    var chainType = 'in an ' + (implicit ? __str_top__ : __str_top__) + ' chain';
    QUnit.test('should follow the iterator protocol ' + chainType, function (assert) {
        assert.expect(3);
        if (!isNpm) {
            var wrapped = chain([
                1,
                2
            ]);
            assert.deepEqual(wrapped.next(), {
                'done': false,
                'value': 1
            });
            assert.deepEqual(wrapped.next(), {
                'done': __bool_top__,
                'value': 2
            });
            assert.deepEqual(wrapped.next(), {
                'done': __bool_top__,
                'value': undefined
            });
        } else {
            skipAssert(assert, 3);
        }
    });
    QUnit.test(__str_top__ + chainType, function (assert) {
        assert.expect(2);
        if (!isNpm && Symbol && Symbol.iterator) {
            var array = [
                    __num_top__,
                    2
                ], wrapped = chain(array);
            assert.strictEqual(wrapped[Symbol.iterator](), wrapped);
            assert.deepEqual(lodashStable.toArray(wrapped), array);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should use `_.toArray` to generate the iterable result ' + chainType, function (assert) {
        assert.expect(3);
        if (!isNpm && Array.from) {
            var hearts = '\uD83D\uDC95', values = [
                    [1],
                    { 'a': 1 },
                    hearts
                ];
            lodashStable.each(values, function (value) {
                var wrapped = chain(value);
                assert.deepEqual(Array.from(wrapped), _.toArray(value));
            });
        } else {
            skipAssert(assert, 3);
        }
    });
    QUnit.test('should reset the iterator correctly ' + chainType, function (assert) {
        assert.expect(4);
        if (!isNpm && Symbol && Symbol.iterator) {
            var array = [
                    __num_top__,
                    2
                ], wrapped = chain(array);
            assert.deepEqual(lodashStable.toArray(wrapped), array);
            assert.deepEqual(lodashStable.toArray(wrapped), [], 'produces an empty array for exhausted iterator');
            var other = wrapped.filter();
            assert.deepEqual(lodashStable.toArray(other), array, __str_top__);
            assert.deepEqual(lodashStable.toArray(wrapped), [], __str_top__);
        } else {
            skipAssert(assert, 4);
        }
    });
    QUnit.test('should work in a lazy sequence ' + chainType, function (assert) {
        assert.expect(3);
        if (!isNpm && Symbol && Symbol.iterator) {
            var array = lodashStable.range(LARGE_ARRAY_SIZE), predicate = function (value) {
                    values.push(value);
                    return isEven(value);
                }, values = [], wrapped = chain(array);
            assert.deepEqual(lodashStable.toArray(wrapped), array);
            wrapped = wrapped.filter(predicate);
            assert.deepEqual(lodashStable.toArray(wrapped), _.filter(array, isEven), 'reset for new lazy chain segments');
            assert.deepEqual(values, array, 'memoizes iterator values');
        } else {
            skipAssert(assert, 3);
        }
    });
});