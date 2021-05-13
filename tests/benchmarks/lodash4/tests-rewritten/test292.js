QUnit.module('lodash(...).next');
lodashStable.each([
    __bool_top__,
    __bool_top__
], function (implicit) {
    function chain(value) {
        return implicit ? _(value) : _.chain(value);
    }
    var chainType = __str_top__ + (implicit ? __str_top__ : __str_top__) + __str_top__;
    QUnit.test(__str_top__ + chainType, function (assert) {
        assert.expect(3);
        if (!isNpm) {
            var wrapped = chain([
                __num_top__,
                __num_top__
            ]);
            assert.deepEqual(wrapped.next(), {
                'done': __bool_top__,
                'value': __num_top__
            });
            assert.deepEqual(wrapped.next(), {
                'done': __bool_top__,
                'value': __num_top__
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
                    __num_top__
                ], wrapped = chain(array);
            assert.strictEqual(wrapped[Symbol.iterator](), wrapped);
            assert.deepEqual(lodashStable.toArray(wrapped), array);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test(__str_top__ + chainType, function (assert) {
        assert.expect(3);
        if (!isNpm && Array.from) {
            var hearts = __str_top__, values = [
                    [__num_top__],
                    { 'a': __num_top__ },
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
    QUnit.test(__str_top__ + chainType, function (assert) {
        assert.expect(4);
        if (!isNpm && Symbol && Symbol.iterator) {
            var array = [
                    __num_top__,
                    __num_top__
                ], wrapped = chain(array);
            assert.deepEqual(lodashStable.toArray(wrapped), array);
            assert.deepEqual(lodashStable.toArray(wrapped), [], __str_top__);
            var other = wrapped.filter();
            assert.deepEqual(lodashStable.toArray(other), array, __str_top__);
            assert.deepEqual(lodashStable.toArray(wrapped), [], __str_top__);
        } else {
            skipAssert(assert, 4);
        }
    });
    QUnit.test(__str_top__ + chainType, function (assert) {
        assert.expect(3);
        if (!isNpm && Symbol && Symbol.iterator) {
            var array = lodashStable.range(LARGE_ARRAY_SIZE), predicate = function (value) {
                    values.push(value);
                    return isEven(value);
                }, values = [], wrapped = chain(array);
            assert.deepEqual(lodashStable.toArray(wrapped), array);
            wrapped = wrapped.filter(predicate);
            assert.deepEqual(lodashStable.toArray(wrapped), _.filter(array, isEven), __str_top__);
            assert.deepEqual(values, array, __str_top__);
        } else {
            skipAssert(assert, 3);
        }
    });
});