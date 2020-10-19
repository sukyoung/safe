QUnit.module('flatMap methods');
lodashStable.each([
    __str_top__,
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], array = [
            __num_top__,
            __num_top__,
            __num_top__,
            __num_top__
        ];
    function duplicate(n) {
        return [
            n,
            n
        ];
    }
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var actual = func(array, duplicate), expected = lodashStable.flatten(lodashStable.map(array, duplicate));
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var objects = [
            {
                'a': [
                    __num_top__,
                    __num_top__
                ]
            },
            {
                'a': [
                    __num_top__,
                    __num_top__
                ]
            }
        ];
        assert.deepEqual(func(objects, __str_top__), array);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = [
                __num_top__,
                __num_top__
            ];
        }
        Foo.prototype.b = [
            __num_top__,
            __num_top__
        ];
        var actual = func(new Foo(), identity);
        assert.deepEqual(actual, [
            __num_top__,
            __num_top__
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var array = [
                [
                    __num_top__,
                    __num_top__
                ],
                [
                    __num_top__,
                    __num_top__
                ]
            ], object = {
                'a': [
                    __num_top__,
                    __num_top__
                ],
                'b': [
                    __num_top__,
                    __num_top__
                ]
            }, values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, lodashStable.constant([
                __num_top__,
                __num_top__,
                __num_top__,
                __num_top__
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
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
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
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        assert.deepEqual(func(__num_top__), []);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = {
            'length': [
                __num_top__,
                __num_top__
            ]
        };
        assert.deepEqual(func(object, identity), [
            __num_top__,
            __num_top__
        ]);
    });
});