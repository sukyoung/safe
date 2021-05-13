QUnit.module('lodash.unzip and lodash.zip');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName, index) {
    var func = _[methodName];
    func = lodashStable.bind(index ? func.apply : func.call, func, null);
    var object = {
        'an empty array': [
            [],
            []
        ],
        '0-tuples': [
            [
                [],
                []
            ],
            []
        ],
        '2-tuples': [
            [
                [
                    __str_top__,
                    __str_top__
                ],
                [
                    __num_top__,
                    __num_top__
                ]
            ],
            [
                [
                    __str_top__,
                    __num_top__
                ],
                [
                    __str_top__,
                    __num_top__
                ]
            ]
        ],
        '3-tuples': [
            [
                [
                    __str_top__,
                    __str_top__
                ],
                [
                    __num_top__,
                    __num_top__
                ],
                [
                    __bool_top__,
                    __bool_top__
                ]
            ],
            [
                [
                    __str_top__,
                    __num_top__,
                    __bool_top__
                ],
                [
                    __str_top__,
                    __num_top__,
                    __bool_top__
                ]
            ]
        ]
    };
    lodashStable.forOwn(object, function (pair, key) {
        QUnit.test(__str_top__ + methodName + __str_top__ + key, function (assert) {
            assert.expect(2);
            var actual = func(pair[__num_top__]);
            assert.deepEqual(actual, pair[__num_top__]);
            assert.deepEqual(func(actual), actual.length ? pair[__num_top__] : []);
        });
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(4);
        var pair = [
            [
                [
                    __str_top__,
                    __num_top__
                ],
                [
                    __str_top__,
                    __num_top__,
                    __bool_top__
                ]
            ],
            [
                [
                    __str_top__,
                    __str_top__
                ],
                [
                    __num_top__,
                    __num_top__
                ],
                [
                    undefined,
                    __bool_top__
                ]
            ]
        ];
        var actual = func(pair[__num_top__]);
        assert.ok(__str_top__ in actual[__num_top__]);
        assert.deepEqual(actual, pair[__num_top__]);
        actual = func(actual);
        assert.ok(__str_top__ in actual[__num_top__]);
        assert.deepEqual(actual, [
            [
                __str_top__,
                __num_top__,
                undefined
            ],
            [
                __str_top__,
                __num_top__,
                __bool_top__
            ]
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var expected = lodashStable.map(falsey, stubArray);
        var actual = lodashStable.map(falsey, function (value) {
            return func([
                value,
                value,
                value
            ]);
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var array = [
            [
                __num_top__,
                __num_top__
            ],
            [
                __num_top__,
                __num_top__
            ],
            null,
            undefined,
            { '0': __num_top__ }
        ];
        assert.deepEqual(func(array), [
            [
                __num_top__,
                __num_top__
            ],
            [
                __num_top__,
                __num_top__
            ]
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var expected = [
            [
                __str_top__,
                __str_top__
            ],
            [
                __num_top__,
                __num_top__
            ]
        ];
        assert.deepEqual(func(func(func(func(expected)))), expected);
    });
});