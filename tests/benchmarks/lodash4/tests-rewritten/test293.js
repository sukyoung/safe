QUnit.module('lodash(...).plant');
(function () {
    QUnit.test('should clone the chained sequence planting `value` as the wrapped value', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var array1 = [
                    __num_top__,
                    null,
                    __num_top__,
                    null,
                    __num_top__
                ], array2 = [
                    __num_top__,
                    null,
                    __num_top__,
                    null,
                    __num_top__
                ], wrapped1 = _(array1).thru(_.compact).map(square).takeRight(__num_top__).sort(), wrapped2 = wrapped1.plant(array2);
            assert.deepEqual(wrapped2.value(), [
                __num_top__,
                __num_top__
            ]);
            assert.deepEqual(wrapped1.value(), [
                __num_top__,
                __num_top__
            ]);
        } else {
            skipAssert(assert, 2);
        }
    });
    QUnit.test('should clone `chainAll` settings', function (assert) {
        assert.expect(1);
        if (!isNpm) {
            var array1 = [
                    __num_top__,
                    __num_top__
                ], array2 = [
                    __num_top__,
                    __num_top__
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
                    __num_top__,
                    __num_top__
                ], array2 = [
                    __num_top__,
                    __num_top__
                ], wrapped1 = _(array1).map(square);
            assert.deepEqual(lodashStable.toArray(wrapped1), [
                __num_top__,
                __num_top__
            ]);
            assert.deepEqual(lodashStable.toArray(wrapped1), []);
            var wrapped2 = wrapped1.plant(array2);
            assert.deepEqual(lodashStable.toArray(wrapped2), [
                __num_top__,
                __num_top__
            ]);
        } else {
            skipAssert(assert, 3);
        }
    });
}());