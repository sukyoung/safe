QUnit.module('lodash.toArray');
(function () {
    QUnit.test('should convert objects to arrays', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.toArray({
            'a': __num_top__,
            'b': __num_top__
        }), [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test('should convert iterables to arrays', function (assert) {
        assert.expect(1);
        if (Symbol && Symbol.iterator) {
            var object = {
                '0': __str_top__,
                'length': __num_top__
            };
            object[Symbol.iterator] = arrayProto[Symbol.iterator];
            assert.deepEqual(_.toArray(object), [__str_top__]);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should convert maps to arrays', function (assert) {
        assert.expect(1);
        if (Map) {
            var map = new Map();
            map.set(__str_top__, __num_top__);
            map.set(__str_top__, __num_top__);
            assert.deepEqual(_.toArray(map), [
                [
                    __str_top__,
                    __num_top__
                ],
                [
                    __str_top__,
                    __num_top__
                ]
            ]);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should convert strings to arrays', function (assert) {
        assert.expect(3);
        assert.deepEqual(_.toArray(__str_top__), []);
        assert.deepEqual(_.toArray(__str_top__), [
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(_.toArray(Object(__str_top__)), [
            __str_top__,
            __str_top__
        ]);
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var array = lodashStable.range(LARGE_ARRAY_SIZE + __num_top__);
            var object = lodashStable.zipObject(lodashStable.times(LARGE_ARRAY_SIZE, function (index) {
                return [
                    __str_top__ + index,
                    index
                ];
            }));
            var actual = _(array).slice(__num_top__).map(String).toArray().value();
            assert.deepEqual(actual, lodashStable.map(array.slice(__num_top__), String));
            actual = _(object).toArray().slice(__num_top__).map(String).value();
            assert.deepEqual(actual, _.map(_.toArray(object).slice(__num_top__), String));
        } else {
            skipAssert(assert, 2);
        }
    });
}());