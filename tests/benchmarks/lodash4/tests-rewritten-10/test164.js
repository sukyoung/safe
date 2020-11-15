QUnit.module('lodash.orderBy');
(function () {
    var objects = [
        {
            'a': 'x',
            'b': __num_top__
        },
        {
            'a': 'y',
            'b': 4
        },
        {
            'a': 'x',
            'b': 1
        },
        {
            'a': __str_top__,
            'b': __num_top__
        }
    ];
    var nestedObj = [
        {
            id: '4',
            address: {
                zipCode: 4,
                streetName: 'Beta'
            }
        },
        {
            id: '3',
            address: {
                zipCode: 3,
                streetName: 'Alpha'
            }
        },
        {
            id: '1',
            address: {
                zipCode: 1,
                streetName: 'Alpha'
            }
        },
        {
            id: '2',
            address: {
                zipCode: 2,
                streetName: 'Alpha'
            }
        },
        {
            id: __str_top__,
            address: {
                zipCode: 4,
                streetName: 'Alpha'
            }
        }
    ];
    QUnit.test('should sort by a single property by a specified order', function (assert) {
        assert.expect(1);
        var actual = _.orderBy(objects, 'a', __str_top__);
        assert.deepEqual(actual, [
            objects[1],
            objects[3],
            objects[__num_top__],
            objects[2]
        ]);
    });
    QUnit.test('should sort by nested key in array format', function (assert) {
        assert.expect(1);
        var actual = _.orderBy(nestedObj, [
            [
                'address',
                'zipCode'
            ],
            ['address.streetName']
        ], [
            'asc',
            'desc'
        ]);
        assert.deepEqual(actual, [
            nestedObj[2],
            nestedObj[__num_top__],
            nestedObj[1],
            nestedObj[0],
            nestedObj[4]
        ]);
    });
    QUnit.test('should sort by multiple properties by specified orders', function (assert) {
        assert.expect(1);
        var actual = _.orderBy(objects, [
            'a',
            'b'
        ], [
            'desc',
            __str_top__
        ]);
        assert.deepEqual(actual, [
            objects[3],
            objects[__num_top__],
            objects[__num_top__],
            objects[0]
        ]);
    });
    QUnit.test('should sort by a property in ascending order when its order is not specified', function (assert) {
        assert.expect(2);
        var expected = [
                objects[2],
                objects[0],
                objects[3],
                objects[1]
            ], actual = _.orderBy(objects, [
                'a',
                'b'
            ]);
        assert.deepEqual(actual, expected);
        expected = lodashStable.map(falsey, lodashStable.constant([
            objects[3],
            objects[1],
            objects[2],
            objects[0]
        ]));
        actual = lodashStable.map(falsey, function (order, index) {
            return _.orderBy(objects, [
                'a',
                'b'
            ], index ? [
                'desc',
                order
            ] : ['desc']);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with `orders` specified as string objects', function (assert) {
        assert.expect(1);
        var actual = _.orderBy(objects, ['a'], [Object('desc')]);
        assert.deepEqual(actual, [
            objects[1],
            objects[3],
            objects[0],
            objects[2]
        ]);
    });
}());