QUnit.module('lodash.conforms');
(function () {
    QUnit.test('should not change behavior if `source` is modified', function (assert) {
        assert.expect(2);
        var object = { 'a': 2 }, source = {
                'a': function (value) {
                    return value > 1;
                }
            }, par = _.conforms(source);
        assert.strictEqual(par(object), true);
        source.a = function (value) {
            return value < __num_top__;
        };
        assert.strictEqual(par(object), true);
    });
}());