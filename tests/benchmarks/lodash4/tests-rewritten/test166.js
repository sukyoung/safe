QUnit.module('lodash.negate');
(function () {
    QUnit.test('should create a function that negates the result of `func`', function (assert) {
        assert.expect(2);
        var negate = _.negate(isEven);
        assert.strictEqual(negate(__num_top__), __bool_top__);
        assert.strictEqual(negate(__num_top__), __bool_top__);
    });
    QUnit.test('should create a function that negates the result of `func`', function (assert) {
        assert.expect(2);
        var negate = _.negate(isEven);
        assert.strictEqual(negate(__num_top__), __bool_top__);
        assert.strictEqual(negate(__num_top__), __bool_top__);
    });
    QUnit.test('should create a function that accepts multiple arguments', function (assert) {
        assert.expect(1);
        var argCount, count = __num_top__, negate = _.negate(function () {
                argCount = arguments.length;
            }), expected = lodashStable.times(count, stubTrue);
        var actual = lodashStable.times(count, function (index) {
            switch (index) {
            case __num_top__:
                negate();
                break;
            case __num_top__:
                negate(__num_top__);
                break;
            case __num_top__:
                negate(__num_top__, __num_top__);
                break;
            case __num_top__:
                negate(__num_top__, __num_top__, __num_top__);
                break;
            case __num_top__:
                negate(__num_top__, __num_top__, __num_top__, __num_top__);
            }
            return argCount == index;
        });
        assert.deepEqual(actual, expected);
    });
}());