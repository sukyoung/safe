QUnit.module('conforms methods');
lodashStable.each([
    __str_top__,
    __str_top__
], function (methodName) {
    var isConforms = methodName == __str_top__;
    function conforms(source) {
        return isConforms ? _.conforms(source) : function (object) {
            return _.conformsTo(object, source);
        };
    }
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var objects = [
            {
                'a': __num_top__,
                'b': __num_top__
            },
            {
                'a': __num_top__,
                'b': __num_top__
            },
            {
                'a': __num_top__,
                'b': __num_top__
            }
        ];
        var par = conforms({
            'b': function (value) {
                return value > __num_top__;
            }
        });
        var actual = lodashStable.filter(objects, par);
        assert.deepEqual(actual, [
            objects[__num_top__],
            objects[__num_top__]
        ]);
        par = conforms({
            'b': function (value) {
                return value > __num_top__;
            },
            'a': function (value) {
                return value > __num_top__;
            }
        });
        actual = lodashStable.filter(objects, par);
        assert.deepEqual(actual, [objects[__num_top__]]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = function (value) {
                return value > __num_top__;
            };
        }
        Foo.prototype.b = function (value) {
            return value > __num_top__;
        };
        var objects = [
            {
                'a': __num_top__,
                'b': __num_top__
            },
            {
                'a': __num_top__,
                'b': __num_top__
            },
            {
                'a': __num_top__,
                'b': __num_top__
            }
        ];
        var par = conforms(new Foo()), actual = lodashStable.filter(objects, par);
        assert.deepEqual(actual, [
            objects[__num_top__],
            objects[__num_top__]
        ]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        var count = __num_top__;
        var par = conforms({
            'a': function () {
                count++;
                return __bool_top__;
            }
        });
        assert.strictEqual(par({}), __bool_top__);
        assert.strictEqual(count, __num_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(2);
        function Foo() {
        }
        Foo.a = __num_top__;
        function Bar() {
        }
        Bar.a = __num_top__;
        var par = conforms({
            'a': function (value) {
                return value > __num_top__;
            }
        });
        assert.strictEqual(par(Foo), __bool_top__);
        assert.strictEqual(par(Bar), __bool_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        function Foo() {
        }
        Foo.a = function (value) {
            return value > __num_top__;
        };
        var objects = [
                { 'a': __num_top__ },
                { 'a': __num_top__ }
            ], actual = lodashStable.filter(objects, conforms(Foo));
        assert.deepEqual(actual, [objects[__num_top__]]);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        function Foo() {
            this.a = __num_top__;
        }
        Foo.prototype.b = __num_top__;
        var par = conforms({
            'b': function (value) {
                return value > __num_top__;
            }
        });
        assert.strictEqual(par(new Foo()), __bool_top__);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, stubFalse);
        var par = conforms({
            'a': function (value) {
                return value > __num_top__;
            }
        });
        var actual = lodashStable.map(values, function (value, index) {
            try {
                return index ? par(value) : par();
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var values = [
                ,
                null,
                undefined
            ], expected = lodashStable.map(values, stubTrue), par = conforms({});
        var actual = lodashStable.map(values, function (value, index) {
            try {
                return index ? par(value) : par();
            } catch (e) {
            }
        });
        assert.deepEqual(actual, expected);
    });
    QUnit.test(__str_top__ + methodName + __str_top__, function (assert) {
        assert.expect(1);
        var object = { 'a': __num_top__ }, expected = lodashStable.map(empties, stubTrue);
        var actual = lodashStable.map(empties, function (value) {
            var par = conforms(value);
            return par(object);
        });
        assert.deepEqual(actual, expected);
    });
});