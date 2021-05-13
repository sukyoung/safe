QUnit.module('lodash.toArray');
(function () {
    QUnit.test('should convert objects to arrays', function (assert) {
        assert.expect(1);
        assert.deepEqual(_.toArray({
            'a': __num_top__,
            'b': 2
        }), [
            1,
            2
        ]);
    });
    QUnit.test('should convert iterables to arrays', function (assert) {
        assert.expect(1);
        if (Symbol && Symbol.iterator) {
            var object = {
                '0': 'a',
                'length': __num_top__
            };
            object[Symbol.iterator] = arrayProto[Symbol.iterator];
            assert.deepEqual(_.toArray(object), ['a']);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should convert maps to arrays', function (assert) {
        assert.expect(1);
        if (Map) {
            var map = new Map();
            map.set(__str_top__, 1);
            map.set('b', 2);
            assert.deepEqual(_.toArray(map), [
                [
                    __str_top__,
                    1
                ],
                [
                    'b',
                    __num_top__
                ]
            ]);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test('should convert strings to arrays', function (assert) {
        assert.expect(3);
        assert.deepEqual(_.toArray(''), []);
        assert.deepEqual(_.toArray(__str_top__), [
            'a',
            'b'
        ]);
        assert.deepEqual(_.toArray(Object('ab')), [
            __str_top__,
            'b'
        ]);
    });
    QUnit.test('should work in a lazy sequence', function (assert) {
        assert.expect(2);
        if (!isNpm) {
            var array = lodashStable.range(LARGE_ARRAY_SIZE + 1);
            var object = lodashStable.zipObject(lodashStable.times(LARGE_ARRAY_SIZE, function (index) {
                return [
                    __str_top__ + index,
                    index
                ];
            }));
            var actual = _(array).slice(1).map(String).toArray().value();
            assert.deepEqual(actual, lodashStable.map(array.slice(1), String));
            actual = _(object).toArray().slice(__num_top__).map(String).value();
            assert.deepEqual(actual, _.map(_.toArray(object).slice(__num_top__), String));
        } else {
            skipAssert(assert, 2);
        }
    });
}());