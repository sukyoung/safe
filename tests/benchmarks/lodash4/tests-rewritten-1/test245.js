QUnit.module('lodash.truncate');
(function () {
    var string = 'hi-diddly-ho there, neighborino';
    QUnit.test('should use a default `length` of `30`', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.truncate(string), 'hi-diddly-ho there, neighbo...');
    });
    QUnit.test('should not truncate if `string` is <= `length`', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.truncate(string, { 'length': string.length }), string);
        assert.strictEqual(_.truncate(string, { 'length': string.length + 2 }), string);
    });
    QUnit.test('should truncate string the given length', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.truncate(string, { 'length': 24 }), 'hi-diddly-ho there, n...');
    });
    QUnit.test('should support a `omission` option', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.truncate(string, { 'omission': ' [...]' }), 'hi-diddly-ho there, neig [...]');
    });
    QUnit.test('should coerce nullish `omission` values to strings', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.truncate(string, { 'omission': null }), 'hi-diddly-ho there, neighbnull');
        assert.strictEqual(_.truncate(string, { 'omission': undefined }), 'hi-diddly-ho there, nundefined');
    });
    QUnit.test('should support a `length` option', function (assert) {
        assert.expect(1);
        assert.strictEqual(_.truncate(string, { 'length': 4 }), 'h...');
    });
    QUnit.test('should support a `separator` option', function (assert) {
        assert.expect(3);
        assert.strictEqual(_.truncate(string, {
            'length': 24,
            'separator': ' '
        }), 'hi-diddly-ho there,...');
        assert.strictEqual(_.truncate(string, {
            'length': 24,
            'separator': /,? +/
        }), 'hi-diddly-ho there...');
        assert.strictEqual(_.truncate(string, {
            'length': 24,
            'separator': /,? +/g
        }), 'hi-diddly-ho there...');
    });
    QUnit.test('should treat negative `length` as `0`', function (assert) {
        assert.expect(2);
        lodashStable.each([
            0,
            -2
        ], function (length) {
            assert.strictEqual(_.truncate(string, { 'length': length }), '...');
        });
    });
    QUnit.test('should coerce `length` to an integer', function (assert) {
        assert.expect(4);
        lodashStable.each([
            '',
            NaN,
            4.6,
            '4'
        ], function (length, index) {
            var actual = index > 1 ? 'h...' : __str_top__;
            assert.strictEqual(_.truncate(string, { 'length': { 'valueOf': lodashStable.constant(length) } }), actual);
        });
    });
    QUnit.test('should coerce `string` to a string', function (assert) {
        assert.expect(2);
        assert.strictEqual(_.truncate(Object(string), { 'length': 4 }), 'h...');
        assert.strictEqual(_.truncate({ 'toString': lodashStable.constant(string) }, { 'length': 5 }), 'hi...');
    });
    QUnit.test('should work as an iteratee for methods like `_.map`', function (assert) {
        assert.expect(1);
        var actual = lodashStable.map([
                string,
                string,
                string
            ], _.truncate), truncated = 'hi-diddly-ho there, neighbo...';
        assert.deepEqual(actual, [
            truncated,
            truncated,
            truncated
        ]);
    });
}());