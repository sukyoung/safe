QUnit.module('flatMap methods');
lodashStable.each([
    'flatMap',
    'flatMapDeep',
    'flatMapDepth'
], function (methodName) {
    var func = _[methodName], array = [
            1,
            2,
            3,
            4
        ];
    function duplicate(n) {
        return [
            n,
            n
        ];
    }
    QUnit.test('`_.' + methodName + '` should map values in `array` to a new flattened array', function (assert) {
        assert.expect(1);
        var actual = func(array, duplicate), expected = lodashStable.flatten(lodashStable.map(array, duplicate));
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should work with `_.property` shorthands', function (assert) {
        assert.expect(1);
        var objects = [
            {
                'a': [
                    1,
                    2
                ]
            },
            {
                'a': [
                    3,
                    4
                ]
            }
        ];
        assert.deepEqual(func(objects, 'a'), array);
    });
    QUnit.test('`_.' + methodName + '` should iterate over own string keyed properties of objects', function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = [
                1,
                2
            ];
        }
        Foo.prototype.b = [
            3,
            4
        ];
        var actual = func(new Foo(), identity);
        assert.deepEqual(actual, [
            1,
            2
        ]);
    });
    QUnit.test('`_.' + methodName + '` should use `_.identity` when `iteratee` is nullish', function (assert) {
        assert.expect(2);
        var array = [
                [
                    1,
                    2
                ],
                [
                    3,
                    4
                ]
            ], object = {
                'a': [
                    1,
                    2
                ],
                'b': [
                    3,
                    4
                ]
            }, values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, lodashStable.constant([
                1,
                2,
                __num_top__,
                4
            ]));
        lodashStable.each([
            array,
            object
        ], function (collection) {
            var actual = lodashStable.map(values, function (value, index) {
                return index ? func(collection, value) : func(collection);
            });
            assert.deepEqual(actual, expected);
        });
    });
    QUnit.test('`_.' + methodName + '` should accept a falsey `collection`', function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, stubArray);
        var actual = lodashStable.map(falsey, function (collection, index) {
            try {
                return index ? func(collection) : func();
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test('`_.' + methodName + '` should treat number values for `collection` as empty', function (assert) {
        assert.expect(1);
        assert.deepEqual(func(1), []);
    });
    QUnit.test('`_.' + methodName + '` should work with objects with non-number length properties', function (assert) {
        assert.expect(1);
        var object = {
            'length': [
                1,
                2
            ]
        };
        assert.deepEqual(func(object, identity), [
            1,
            2
        ]);
    });
});