QUnit.module('lodash.partition');
(function () {
    var array = [
        __num_top__,
        __num_top__,
        __num_top__
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
                    __num_top__
                ],
                [__num_top__]
            ]));
        var actual = lodashStable.map(values, function (value, index) {
            return index ? _.partition(array, value) : _.partition(array);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        var objects = [
                { 'a': __num_top__ },
                { 'a': __num_top__ },
                { 'b': __num_top__ }
            ], actual = _.partition(objects, __str_top__);
        assert.deepEqual(actual, [
            objects.slice(__num_top__, __num_top__),
            objects.slice(__num_top__)
        ]);
    });
    QUnit.test('should work with a number for `predicate`', function (assert) {
        assert.expect(2);
        var array = [
            [
                __num_top__,
                __num_top__
            ],
            [
                __num_top__,
                __num_top__
            ],
            [
                __num_top__,
                __num_top__
            ]
        ];
        assert.deepEqual(_.partition(array, __num_top__), [
            [
                array[__num_top__],
                array[__num_top__]
            ],
            [array[__num_top__]]
        ]);
        assert.deepEqual(_.partition(array, __num_top__), [
            [array[__num_top__]],
            [
                array[__num_top__],
                array[__num_top__]
            ]
        ]);
    });
    QUnit.test('should work with an object for `collection`', function (assert) {
        assert.expect(1);
        var actual = _.partition({
            'a': __num_top__,
            'b': __num_top__,
            'c': __num_top__
        }, Math.floor);
        assert.deepEqual(actual, [
            [
                __num_top__,
                __num_top__
            ],
            [__num_top__]
        ]);
    });
}());