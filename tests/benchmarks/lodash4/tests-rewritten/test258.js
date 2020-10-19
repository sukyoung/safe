QUnit.module('toPairs methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var func = _[methodName], isToPairs = methodName == __str_top__;
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = {
                'a': __num_top__,
                'b': __num_top__
            }, actual = lodashStable.sortBy(func(object), __num_top__);
        assert.deepEqual(actual, [
            [
                __str_top__,
                __num_top__
            ],
            [
                __str_top__,
                __num_top__
            ]
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__ + (isToPairs ? __str_top__ : __str_top__) + __str_top__, function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = __num_top__;
        }
        Foo.prototype.b = __num_top__;
        var expected = isToPairs ? [[
                    __str_top__,
                    __num_top__
                ]] : [
                [
                    __str_top__,
                    __num_top__
                ],
                [
                    __str_top__,
                    __num_top__
                ]
            ], actual = lodashStable.sortBy(func(new Foo()), __num_top__);
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = {
                '0': __str_top__,
                '1': __str_top__,
                'length': __num_top__
            }, actual = lodashStable.sortBy(func(object), __num_top__);
        assert.deepEqual(actual, [
            [
                __str_top__,
                __str_top__
            ],
            [
                __str_top__,
                __str_top__
            ],
            [
                __str_top__,
                __num_top__
            ]
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (Map) {
            var map = new Map();
            map.set(__str_top__, __num_top__);
            map.set(__str_top__, __num_top__);
            assert.deepEqual(func(map), [
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
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        if (Set) {
            var set = new Set();
            set.add(__num_top__);
            set.add(__num_top__);
            assert.deepEqual(func(set), [
                [
                    __num_top__,
                    __num_top__
                ],
                [
                    __num_top__,
                    __num_top__
                ]
            ]);
        } else {
            skipAssert(assert);
        }
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        lodashStable.each([
            __str_top__,
            Object(__str_top__)
        ], function (string) {
            var actual = lodashStable.sortBy(func(string), __num_top__);
            assert.deepEqual(actual, [
                [
                    __str_top__,
                    __str_top__
                ],
                [
                    __str_top__,
                    __str_top__
                ]
            ]);
        });
    });
});