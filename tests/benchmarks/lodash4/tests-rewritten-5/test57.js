QUnit.module('strict mode checks');
lodashStable.each([
    __str_top__,
    'assignIn',
    'bindAll',
    'defaults',
    'defaultsDeep',
    __str_top__
], function (methodName) {
    var func = _[methodName], isBindAll = methodName == 'bindAll';
    QUnit.test(__str_top__ + methodName + __str_top__ + (isStrict ? __str_top__ : 'not ') + 'throw strict mode errors', function (assert) {
        assert.expect(1);
        var object = freeze({
                'a': undefined,
                'b': function () {
                }
            }), pass = !isStrict;
        try {
            func(object, isBindAll ? 'b' : { 'a': 1 });
        } catch (e) {
            pass = !pass;
        }
        assert.ok(pass);
    });
});