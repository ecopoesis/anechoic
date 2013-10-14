define(['lib/cookie', 'pidcrypt_rsa', 'pidcrypt_asn1'], function(cookie) {
    var encryption = {
        encrypt: function(cleartext) {
            var rsa = new pidCrypt.RSA();
            var cert = encryption.certParser(encryption.public_key());
            var asn = pidCrypt.ASN1.decode(pidCryptUtil.toByteArray(pidCryptUtil.decodeBase64(cert.b64)));
            var tree = asn.toHexTree();
            rsa.setPublicKeyFromASN(tree);
            var encrypted = rsa.encrypt(cleartext);
            cookie.bake('q', encrypted, 9125)
        },

        certParser: function(cert) {
            var lines = cert.split('\n');
            var read = false;
            var b64 = false;
            var end = false;
            var flag = '';
            var retObj = {};
            retObj.info = '';
            retObj.salt = '';
            retObj.iv;
            retObj.b64 = '';
            retObj.aes = false;
            retObj.mode = '';
            retObj.bits = 0;
            for(var i=0; i< lines.length; i++){
                flag = lines[i].substr(0,9);
                if(i==1 && flag != 'Proc-Type' && flag.indexOf('M') == 0) // unencrypted cert?
                    b64 = true;
                switch(flag){
                    case '-----BEGI':
                        read = true;
                        break;
                    case 'Proc-Type':
                        if(read)
                            retObj.info = lines[i];
                        break;
                    case 'DEK-Info:':
                        if(read){
                            var tmp = lines[i].split(',');
                            var dek = tmp[0].split(': ');
                            var aes = dek[1].split('-');
                            retObj.aes = (aes[0] == 'AES')?true:false;
                            retObj.mode = aes[2];
                            retObj.bits = parseInt(aes[1]);
                            retObj.salt = tmp[1].substr(0,16);
                            retObj.iv = tmp[1];
                        }
                        break;
                    case '':
                        if(read)
                            b64 = true;
                        break;
                    case '-----END ':
                        if(read){
                            b64 = false;
                            read = false;
                        }
                        break;
                    default:
                        if(read && b64)
                            retObj.b64 += pidCryptUtil.stripLineFeeds(lines[i]);
                }
            }
            return retObj;
        },

        public_key: function() {
            return '-----BEGIN PUBLIC KEY-----\n\
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAovOedBskkv8zzpu820XO\n\
w38/4mqBnRz0GpUUO+z+jRcNmFf2fMdOD/KQrCorNjT8wjuLQdlvVEUdoAp1DHlG\n\
5YhEHozhLaM4EzQg4td+Ni0kEuikMw6OZb5lGEdTXnN+g5VRTw6kNhQHMGwa2RD6\n\
//er7uV16U0DCjK3HnEP6lGrh0qKDpin97swADu/aEQsBsvBeWzmOvo7SLupevTx\n\
ciEWwbYmLQgaXenPAHyV4VoHMX4B7HxyU6jJqO72HfeJik8jFD1dcKwjVKJXV0K8\n\
zLQ9woNQ/fwL30+xA334njrHOWy0VrPxtP6AdE6FdhLiQxj+ADeFJD+LuKsTlCqH\n\
AQIDAQAB\n\
-----END PUBLIC KEY-----'
        }
    }

    return encryption;
});