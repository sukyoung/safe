QUnit.module('strict mode checks');
lodashStable.each([
    'assign',
    __str_top__,
    __str_top__,
    'defaults',
    'defaultsDeep',
    'merge'
], function (methodName) {
    var func = _[methodName], isBindAll = methodName == 'bindAll';
    QUnit.test('`_.' + methodName + '` should ' + (isStrict ? '' : __str_top__) + __str_top__, function (assert) {
        assert.expect(1);
        var object = freeze({
                'a': undefined,
                'b': function () {
                }
            }), pass = !isStrict;
        try {
            func(object, isBindAll ? __str_top__ : { 'a': 1 });
        } catch (e) {
            pass = !pass;
        }
        assert.ok(pass);
    });
});