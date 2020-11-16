QUnit.module('lodash(...).plant');
(function () {
    QUnit.test('should clone the chained sequence planting `value` as the wrapped value', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var array1 = [
                    5,
                    null,
                    __num_top__,
                    null,
                    __num_top__
                ], array2 = [
                    10,
                    null,
                    __num_top__,
                    null,
                    __num_top__
                ], wrapped1 = _(array1).thru(_.compact).map(square).takeRight(2).sort(), wrapped2 = wrapped1.plant(array2);
            assert.deepEqual(wrapped2.value(), [
                36,
                64
            ]);
            assert.deepEqual(wrapped1.value(), [
                __num_top__,
                9
            ]);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should clone `chainAll` settings', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var array1 = [
                    2,
                    4
                ], array2 = [
                    6,
                    8
                ], wrapped1 = _(array1).chain().map(square), wrapped2 = wrapped1.plant(array2);
            assert.deepEqual(wrapped2.head().value(), __num_top__);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should reset iterator data on cloned sequences', function (assert) {
        assert.expect(3);
        if (!isNpm && Symbol && Symbol.iterator) {
            var array1 = [
                    2,
                    4
                ], array2 = [
                    __num_top__,
                    8
                ], wrapped1 = _(array1).map(square);
            assert.deepEqual(lodashStable.toArray(wrapped1), [
                __num_top__,
                16
            ]);
            assert.deepEqual(lodashStable.toArray(wrapped1), []);
            var wrapped2 = wrapped1.plant(array2);
            assert.deepEqual(lodashStable.toArray(wrapped2), [
                __num_top__,
                64
            ]);
        } else {
            skipAssert(assert, 3);
        }
    });
}());