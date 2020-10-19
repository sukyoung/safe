QUnit.module('lodash.orderBy');
(function () {
    var objects = [
        {
            'a': __str_top__,
            'b': __num_top__
        },
        {
            'a': __str_top__,
            'b': __num_top__
        },
        {
            'a': __str_top__,
            'b': __num_top__
        },
        {
            'a': __str_top__,
            'b': __num_top__
        }
    ];
    var nestedObj = [
        {
            id: __str_top__,
            address: {
                zipCode: __num_top__,
                streetName: __str_top__
            }
        },
        {
            id: __str_top__,
            address: {
                zipCode: __num_top__,
                streetName: __str_top__
            }
        },
        {
            id: __str_top__,
            address: {
                zipCode: __num_top__,
                streetName: __str_top__
            }
        },
        {
            id: __str_top__,
            address: {
                zipCode: __num_top__,
                streetName: __str_top__
            }
        },
        {
            id: __str_top__,
            address: {
                zipCode: __num_top__,
                streetName: __str_top__
            }
        }
    ];
    QUnit.test('should sort by a single property by a specified order', function (assert) {
        assert.expect(1);
        var actual = _.orderBy(objects, __str_top__, __str_top__);
        assert.deepEqual(actual, [
            objects[__num_top__],
            objects[__num_top__],
            objects[__num_top__],
            objects[__num_top__]
        ]);
    });
    QUnit.test('should sort by nested key in array format', function (assert) {
        assert.expect(1);
        var actual = _.orderBy(nestedObj, [
            [
                __str_top__,
                __str_top__
            ],
            [__str_top__]
        ], [
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(actual, [
            nestedObj[__num_top__],
            nestedObj[__num_top__],
            nestedObj[__num_top__],
            nestedObj[__num_top__],
            nestedObj[__num_top__]
        ]);
    });
    QUnit.test('should sort by multiple properties by specified orders', function (assert) {
        assert.expect(1);
        var actual = _.orderBy(objects, [
            __str_top__,
            __str_top__
        ], [
            __str_top__,
            __str_top__
        ]);
        assert.deepEqual(actual, [
            objects[__num_top__],
            objects[__num_top__],
            objects[__num_top__],
            objects[__num_top__]
        ]);
    });
    QUnit.test('should sort by a property in ascending order when its order is not specified', function (assert) {
        assert.expect(2);
        var expected = [
                objects[__num_top__],
                objects[__num_top__],
                objects[__num_top__],
                objects[__num_top__]
            ], actual = _.orderBy(objects, [
                __str_top__,
                __str_top__
            ]);
        assert.deepEqual(actual, expected);
        expected = lodashStable.map(falsey, lodashStable.constant([
            objects[__num_top__],
            objects[__num_top__],
            objects[__num_top__],
            objects[__num_top__]
        ]));
        actual = lodashStable.map(falsey, function (order, index) {
            return _.orderBy(objects, [
                __str_top__,
                __str_top__
            ], index ? [
                __str_top__,
                order
            ] : [__str_top__]);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with `orders` specified as string objects', function (assert) {
        assert.expect(1);
        var actual = _.orderBy(objects, [__str_top__], [Object(__str_top__)]);
        assert.deepEqual(actual, [
            objects[__num_top__],
            objects[__num_top__],
            objects[__num_top__],
            objects[__num_top__]
        ]);
    });
}());