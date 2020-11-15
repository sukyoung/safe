QUnit.module('lodash.conforms');
(function () {
    QUnit.test('should not change behavior if `source` is modified', function (assert) {
        assert.expect(2);
        var object = { 'a': __num_top__ }, source = {
                'a': function (value) {
                    return value > __num_top__;
                }
            }, par = _.conforms(source);
        assert.strictEqual(par(object), __bool_top__);
        source.a = function (value) {
            return value < __num_top__;
        };
        assert.strictEqual(par(object), __bool_top__);
    });
}());