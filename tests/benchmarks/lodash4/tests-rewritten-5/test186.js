QUnit.module('lodash.partition');
(function () {
    var array = [
        1,
        __num_top__,
        1
    ];
    QUnit.test('should split elements into two groups by `predicate`', function (assert) {
        assert.expect(3);
        assert.deepEqual(_.partition([], identity), [
            [],
            []
        ]);
        assert.deepEqual(_.partition(array, stubTrue), [
            array,
            []
        ]);
        assert.deepEqual(_.partition(array, stubFalse), [
            [],
            array
        ]);
    });
    QUnit.test('should use `_.identity` when `predicate` is nullish', function (assert) {
        assert.expect(1);
        var values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, lodashStable.constant([
                [
                    __num_top__,
                    1
                ],
                [0]
            ]));
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.partition(array, value) : _.partition(array);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        var objects = [
                { 'a': 1 },
                { 'a': 1 },
                { 'b': __num_top__ }
            ], actual = _.partition(objects, 'a');
        assert.deepEqual(actual, [
            objects.slice(0, 2),
            objects.slice(2)
        ]);
    });
    QUnit.test('should work with a number for `predicate`', function (assert) {
        assert.expect(2);
        var array = [
            [
                1,
                0
            ],
            [
                0,
                __num_top__
            ],
            [
                1,
                0
            ]
        ];
        assert.deepEqual(_.partition(array, 0), [
            [
                array[0],
                array[2]
            ],
            [array[1]]
        ]);
        assert.deepEqual(_.partition(array, 1), [
            [array[__num_top__]],
            [
                array[0],
                array[2]
            ]
        ]);
    });
    QUnit.test('should work with an object for `collection`', function (assert) {
        assert.expect(1);
        var actual = _.partition({
            'a': 1.1,
            'b': 0.2,
            'c': 1.3
        }, Math.floor);
        assert.deepEqual(actual, [
            [
                1.1,
                1.3
            ],
            [0.2]
        ]);
    });
}());