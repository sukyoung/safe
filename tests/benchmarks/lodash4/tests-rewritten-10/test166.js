QUnit.module('lodash.negate');
(function () {
    QUnit.test('should create a function that negates the result of `func`', function (assert) {
        assert.expect(2);
        var negate = _.negate(isEven);
        assert.strictEqual(negate(1), __bool_top__);
        assert.strictEqual(negate(2), __bool_top__);
    });
    QUnit.test('should create a function that negates the result of `func`', function (assert) {
        assert.expect(2);
        var negate = _.negate(isEven);
        assert.strictEqual(negate(1), true);
        assert.strictEqual(negate(__num_top__), false);
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
                negate(1);
                break;
            case 2:
                negate(1, __num_top__);
                break;
            case 3:
                negate(1, 2, __num_top__);
                break;
            case 4:
                negate(1, __num_top__, __num_top__, 4);
            }
            return argCount == index;
        });
        assert.deepEqual(actual, expected);
    });
}());