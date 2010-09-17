ideasaurus.util.Utility = function(){
};

ideasaurus.util.Utility.prototype = {
    randomString : function(len) {
        var result = "";
        for (var i = 0; i < len; ++i) {
            var num = Math.floor(Math.random() * this._keyStr.length);
            result += this._keyStr.substring(num,num+1);
        }
        return result;
    },
    // Cookie utilities
    createCookie : function(name,value,expires) {
        document.cookie = name+"="+value+";expires="+expires+"; path=/";
    },
    readCookie : function(name) {
        var nameEQ = name + "=";
        var ca = document.cookie.split(';');
        for (var i = 0; i < ca.length; ++i) {
            var c = ca[i];
            while (c.charAt(0)==' ') {
                c = c.substring(1,c.length);
            }
            if (c.indexOf(nameEQ) == 0) {
                return c.substring(nameEQ.length,c.length);
            }
        }
        return null;
    },
    removeCookie : function(name) {
        var now = new Date();
        now.setTime(now.getTime()-(1000*60*60*24));
        this.createCookie(name,"",now.toGMTString());
    },
    // TEA encryption algorithm
    encrypt : function(plaintext, password) {
        if (plaintext.length == 0) return('');  // nothing to encrypt
        // 'escape' plaintext so chars outside ISO-8859-1 work in single-byte packing, but keep
        // spaces as spaces (not '%20') so encrypted text doesn't grow too long (quick & dirty)
        var asciitext = escape(plaintext).replace(/%20/g,' ');
        var v = this.strToLongs(asciitext);  // convert string to array of longs
        if (v.length <= 1) v[1] = 0;  // algorithm doesn't work for n<2 so fudge by adding a null
        var k = this.strToLongs(password.slice(0,16));  // simply convert first 16 chars of password as key
        var n = v.length;

        var z = v[n-1], y = v[0], delta = 0x9E3779B9;
        var mx, e, q = Math.floor(6 + 52/n), sum = 0;

        while (q-- > 0) {  // 6 + 52/n operations gives between 6 & 32 mixes on each word
            sum += delta;
            e = sum>>>2 & 3;
            for (var p = 0; p < n; p++) {
                y = v[(p+1)%n];
                mx = (z>>>5 ^ y<<2) + (y>>>3 ^ z<<4) ^ (sum^y) + (k[p&3 ^ e] ^ z);
                z = v[p] += mx;
            }
        }

        var ciphertext = this.longsToStr(v);

        return this.escCtrlCh(ciphertext);
    },
    decrypt : function(ciphertext, password) {
        if (ciphertext.length == 0) return('');
        var v = this.strToLongs(this.unescCtrlCh(ciphertext));
        var k = this.strToLongs(password.slice(0,16));
        var n = v.length;

        var z = v[n-1], y = v[0], delta = 0x9E3779B9;
        var mx, e, q = Math.floor(6 + 52/n), sum = q*delta;

        while (sum != 0) {
            e = sum>>>2 & 3;
            for (var p = n-1; p >= 0; p--) {
                z = v[p>0 ? p-1 : n-1];
                mx = (z>>>5 ^ y<<2) + (y>>>3 ^ z<<4) ^ (sum^y) + (k[p&3 ^ e] ^ z);
                y = v[p] -= mx;
            }
            sum -= delta;
        }

        var plaintext = this.longsToStr(v);

        // strip trailing null chars resulting from filling 4-char blocks:
        plaintext = plaintext.replace(/\0+$/,'');

        return unescape(plaintext);
    },
    strToLongs : function(s) {  // convert string to array of longs, each containing 4 chars
        // note chars must be within ISO-8859-1 (with Unicode code-point < 256) to fit 4/long
        var l = new Array(Math.ceil(s.length/4));
        for (var i=0; i<l.length; i++) {
            // note little-endian encoding - endianness is irrelevant as long as
            // it is the same in longsToStr()
            l[i] = s.charCodeAt(i*4) + (s.charCodeAt(i*4+1)<<8) +
                   (s.charCodeAt(i*4+2)<<16) + (s.charCodeAt(i*4+3)<<24);
        }
        return l;  // note running off the end of the string generates nulls since
    },              // bitwise operators treat NaN as 0
    longsToStr : function(l) {  // convert array of longs back to string
        var a = new Array(l.length);
        for (var i=0; i<l.length; i++) {
            a[i] = String.fromCharCode(l[i] & 0xFF, l[i]>>>8 & 0xFF,
                                       l[i]>>>16 & 0xFF, l[i]>>>24 & 0xFF);
        }
        return a.join('');  // use Array.join() rather than repeated string appends for efficiency
    },
    escCtrlCh : function(str) {  // escape control chars etc which might cause problems with encrypted texts
        return str.replace(/[\0\t\n\v\f\r\xa0'"!]/g, function(c) { return '!' + c.charCodeAt(0) + '!'; });
    },
    unescCtrlCh : function(str) {  // unescape potentially problematic nulls and control characters
        return str.replace(/!\d\d?\d?!/g, function(c) { return String.fromCharCode(c.slice(1,-1)); });
    },
    
    
    // Base64 encoding
	// private property
	_keyStr : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

	// public method for encoding
	base64encode : function (input) {
		var output = "";
		var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
		var i = 0;

		input = this._utf8_encode(input);

		while (i < input.length) {

			chr1 = input.charCodeAt(i++);
			chr2 = input.charCodeAt(i++);
			chr3 = input.charCodeAt(i++);

			enc1 = chr1 >> 2;
			enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
			enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
			enc4 = chr3 & 63;

			if (isNaN(chr2)) {
				enc3 = enc4 = 64;
			} else if (isNaN(chr3)) {
				enc4 = 64;
			}

			output = output +
			this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) +
			this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);

		}

		return output;
	},

	// public method for decoding
	base64decode : function (input) {
		var output = "";
		var chr1, chr2, chr3;
		var enc1, enc2, enc3, enc4;
		var i = 0;

		input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

		while (i < input.length) {

			enc1 = this._keyStr.indexOf(input.charAt(i++));
			enc2 = this._keyStr.indexOf(input.charAt(i++));
			enc3 = this._keyStr.indexOf(input.charAt(i++));
			enc4 = this._keyStr.indexOf(input.charAt(i++));

			chr1 = (enc1 << 2) | (enc2 >> 4);
			chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
			chr3 = ((enc3 & 3) << 6) | enc4;

			output = output + String.fromCharCode(chr1);

			if (enc3 != 64) {
				output = output + String.fromCharCode(chr2);
			}
			if (enc4 != 64) {
				output = output + String.fromCharCode(chr3);
			}

		}

		output = this._utf8_decode(output);

		return output;

	},

	// private method for UTF-8 encoding
	_utf8_encode : function (string) {
		string = string.replace(/\r\n/g,"\n");
		var utftext = "";

		for (var n = 0; n < string.length; n++) {

			var c = string.charCodeAt(n);

			if (c < 128) {
				utftext += String.fromCharCode(c);
			}
			else if((c > 127) && (c < 2048)) {
				utftext += String.fromCharCode((c >> 6) | 192);
				utftext += String.fromCharCode((c & 63) | 128);
			}
			else {
				utftext += String.fromCharCode((c >> 12) | 224);
				utftext += String.fromCharCode(((c >> 6) & 63) | 128);
				utftext += String.fromCharCode((c & 63) | 128);
			}

		}

		return utftext;
	},

	// private method for UTF-8 decoding
	_utf8_decode : function (utftext) {
		var string = "";
		var i = 0;
		var c = c1 = c2 = 0;

		while ( i < utftext.length ) {

			c = utftext.charCodeAt(i);

			if (c < 128) {
				string += String.fromCharCode(c);
				i++;
			}
			else if((c > 191) && (c < 224)) {
				c2 = utftext.charCodeAt(i+1);
				string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
				i += 2;
			}
			else {
				c2 = utftext.charCodeAt(i+1);
				c3 = utftext.charCodeAt(i+2);
				string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
				i += 3;
			}

		}

		return string;
	}
};