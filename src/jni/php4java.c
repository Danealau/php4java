#include <sapi/embed/php_embed.h>
#include <ext/json/php_json.h>
#include <Zend/zend_smart_str.h>
#include <Zend/zend_smart_str_public.h>
#include <Zend/zend_exceptions.h>

#include "php4java_Native_Php.h"
#include "php4java_Native_Php.h"

/** Convert a Zval Java object to a real zval */
static zval* obj2zval(JNIEnv *env, jobject obj) {
    jclass clazz;
    jfieldID field;
    jlong addr;

    clazz = (*env)->GetObjectClass(env, obj);
    field = (*env)->GetFieldID(env, clazz, "zvalAddr", "J");
    addr = (*env)->GetLongField(env, obj, field);

    return (zval*)addr;
}

/** Convert a real zval to a Zval Java object */
static jobject zval2obj(JNIEnv *env, zval *z) {
    jclass clazz;
    jmethodID ctor;
    jobject obj;
    jfieldID field;
    zval *zdup;

    clazz = (*env)->FindClass(env, "php4java/Native/Zval");
    ctor = (*env)->GetMethodID(env, clazz, "<init>", "()V");
    obj = (*env)->NewObject(env, clazz, ctor);
    field = (*env)->GetFieldID(env, clazz, "zvalAddr", "J");

    zdup = (zval*)emalloc(sizeof(zval)); // freed in dispose()
    ZVAL_DUP(zdup, z);
    (*env)->SetLongField(env, obj, field, (jlong)zdup);

    return obj;
}

/*
 * Class:     php4java_Php
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_php4java_Native_Php_init(JNIEnv *env, jclass cls) {
    php_embed_init(0, NULL);
}

/*
 * Class:     php4java_Php
 * Method:    shutdown
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_php4java_Native_Php_shutdown(JNIEnv *env, jclass cls) {
    php_embed_shutdown();
}

#define GET_PROPERTY(object, id) \
	zend_read_property_ex(i_get_exception_base(object), (object), ZSTR_KNOWN(id), 0, &rv)
#define GET_PROPERTY_SILENT(object, id) \
	zend_read_property_ex(i_get_exception_base(object), (object), ZSTR_KNOWN(id), 1, &rv)

static inline zend_class_entry *i_get_exception_base(zval *object)
{
	return instanceof_function(Z_OBJCE_P(object), zend_ce_exception) ? zend_ce_exception : zend_ce_error;
}

void __process_php4java_exception(JNIEnv *env)
{
    zval excval, rv;
    zend_class_entry *ce_exception;

    // Get object from exception
    ZVAL_OBJ(&excval, EG(exception));

    // Get ce
    ce_exception = EG(exception)->ce;

    // Clean global exception
    EG(exception) = NULL;

    if (ce_exception == zend_ce_parse_error || ce_exception == zend_ce_compile_error || instanceof_function(ce_exception, zend_ce_throwable))
    {
        // Get message from exception
        zend_string* message = zval_get_string(GET_PROPERTY(&excval, ZEND_STR_MESSAGE));

        // Get file name from exception
        zend_string* file = zval_get_string(GET_PROPERTY_SILENT(&excval, ZEND_STR_FILE));

        // Get source line number from exception
	    zend_long line = zval_get_long(GET_PROPERTY_SILENT(&excval, ZEND_STR_LINE));

        char* err_str_ptr = NULL;
        if (ce_exception == zend_ce_parse_error || ce_exception == zend_ce_compile_error)
        {
            bool is_parse_err = (ce_exception == zend_ce_parse_error);

            const char* msg = "[php4java : PHP-JNI : eval] Internal %s exception caught!\n>> Message: %s\n>> File: %s\n>> Line: %d";
            ssize_t bufsz = snprintf(NULL, 0, msg, is_parse_err ? "parsing" : "compilation", ZSTR_VAL(message), ZSTR_VAL(file), line);
            err_str_ptr = malloc(bufsz + 1);
            snprintf(err_str_ptr, bufsz + 1, msg, is_parse_err ? "parsing" : "compilation", ZSTR_VAL(message), ZSTR_VAL(file), line);
        }
        else
        {
            const char* msg = "[php4java : PHP-JNI : eval] PHP runtime throwable exception caught!\n>> Message: %s\n>> File: %s\n>> Line: %d";
            ssize_t bufsz = snprintf(NULL, 0, msg, ZSTR_VAL(message), ZSTR_VAL(file), line);
            err_str_ptr = malloc(bufsz + 1);
            snprintf(err_str_ptr, bufsz + 1, msg, ZSTR_VAL(message), ZSTR_VAL(file), line);
        }

        // free memory with strings
        zend_string_release_ex(file, 0);
        zend_string_release_ex(message, 0);

        // Throw Java exception
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), err_str_ptr);
    }
    else
    {
        // Throw Java exception
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), "[php4java : PHP-JNI : eval] PHP unknown exception encountered!");
    }
}

/*
 * Class:     php4java_Php
 * Method:    execString
 * Signature: (Ljava/lang/String;)Lphp4java/Native/Zval;
 */
JNIEXPORT jobject JNICALL Java_php4java_Native_Php__1_1eval(JNIEnv *env, jclass cls, jstring jcode) {
    zval retval;
    const char *code;
    char *result;
    jobject obj;
    int eval_result;

    code = (*env)->GetStringUTFChars(env, jcode, 0);

    // Try to "eval(...)" code in PHP engine
    zend_first_try {

        eval_result = zend_eval_string_ex((char*)code, &retval, "php4java", 0);
        if (EG(exception))
            __process_php4java_exception(env);
        
    } zend_catch {

        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), "[php4java : PHP-JNI : eval] Unknown <zend_catch> exception encountered!");

    } zend_end_try();

    (*env)->ReleaseStringUTFChars(env, jcode, code);

    obj = zval2obj(env, &retval);
    zval_ptr_dtor(&retval);

    return obj;
}

/*
 * Class:     php4java_Zval
 * Method:    getLong
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_php4java_Native_Zval_getLong(JNIEnv *env, jobject obj) {
    zval *z;
    z = obj2zval(env, obj);
    return z ? zval_get_long(z) : 0;
}

/*
 * Class:     php4java_Zval
 * Method:    getDouble
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_php4java_Native_Zval_getDouble(JNIEnv *env, jobject obj) {
    zval *z;
    z = obj2zval(env, obj);
    return z ? zval_get_double(z) : 0.0;
}

/*
 * Class:     php4java_Zval
 * Method:    getBoolean
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_php4java_Native_Zval_getBoolean(JNIEnv *env, jobject obj) {
    zval *z;
    z = obj2zval(env, obj);
    return z ? zval_is_true(z) : 0;
}

/*
 * Class:     php4java_Zval
 * Method:    getString
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_php4java_Native_Zval_getString(JNIEnv *env, jobject obj) {
    zval *z;
    zend_string *str;
    jstring jstr;

    z = obj2zval(env, obj);
    if (!z) {
        return NULL;
    }

    str = zval_get_string(z);
    jstr = (*env)->NewStringUTF(env, ZSTR_VAL(str));
    zend_string_release(str);

    return jstr;
}

/*
 * Class:     php4java_Zval
 * Method:    getArray
 * Signature: ()[Lphp4java/Native/Zval;
 */
JNIEXPORT jobjectArray JNICALL Java_php4java_Native_Zval_getArray(JNIEnv *env, jobject obj) {
    zval *z;
    zval zcopy;
    jobjectArray array;
    jclass clazz;
    HashTable *array_ht;
    zend_long array_len;
    zend_string *array_key;
    jsize array_i;
    zval *array_val;

    z = obj2zval(env, obj);
    if (!z) {
        return NULL;
    }

    ZVAL_COPY(&zcopy, z);
    convert_to_array(&zcopy);

    clazz = (*env)->FindClass(env, "php4java/Native/Zval");

    array_ht = Z_ARRVAL(zcopy);
    array_len = zend_array_count(array_ht);
    array = (*env)->NewObjectArray(env, array_len, clazz, NULL);
    array_i = 0;
    ZEND_HASH_FOREACH_VAL(array_ht, array_val) {
        (*env)->SetObjectArrayElement(env, array, array_i, zval2obj(env, array_val));
        array_i += 1;
    } ZEND_HASH_FOREACH_END();
    zval_ptr_dtor(&zcopy);

    return array;
}

/*
 * Class:     php4java_Zval
 * Method:    getHash
 * Signature: ()Ljava/util/Map;
 */
JNIEXPORT jobject JNICALL Java_php4java_Native_Zval_getHash(JNIEnv *env, jobject obj) {
    zval *z;
    zval zcopy;
    jobject map;
    jmethodID ctor;
    jmethodID put;
    jclass clazz;
    HashTable *array_ht;
    zend_string *array_key;
    zend_long array_i;
    zval *array_val;

    z = obj2zval(env, obj);
    if (!z) {
        return NULL;
    }

    ZVAL_COPY(&zcopy, z);
    convert_to_array(&zcopy);

    clazz = (*env)->FindClass(env, "java/util/HashMap");
    ctor = (*env)->GetMethodID(env, clazz, "<init>", "()V");
    put = (*env)->GetMethodID(env, clazz, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
    map = (*env)->NewObject(env, clazz, ctor);

    array_ht = Z_ARRVAL(zcopy);
    ZEND_HASH_FOREACH_KEY_VAL(array_ht, array_i, array_key, array_val) {
        (*env)->CallObjectMethod(env, map, put,
            (*env)->NewStringUTF(env, ZSTR_VAL(array_key)),
            zval2obj(env, array_val)
        );
    } ZEND_HASH_FOREACH_END();
    zval_ptr_dtor(&zcopy);

    return map;
}

/*
 * Class:     php4java_Zval
 * Method:    getJson
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_php4java_Native_Zval_getJson(JNIEnv *env, jobject obj) {
    char *json;
    jstring jstr;
    smart_str buf = {0};
    zval *z;

    z = obj2zval(env, obj);
    if (!z) {
        return NULL;
    }

    php_json_encode(&buf, z, 0);
    smart_str_0(&buf);
    jstr = (*env)->NewStringUTF(env, ZSTR_VAL(buf.s));
    smart_str_free(&buf);

    return jstr;
}

/*
 * Class:     php4java_Zval
 * Method:    dispose
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_php4java_Native_Zval_dispose(JNIEnv *env, jobject obj) {
    zval *z;
    jclass clazz;
    jfieldID field;
    z = obj2zval(env, obj);
    if (!z) {
        return;
    }
    clazz = (*env)->GetObjectClass(env, obj);
    field = (*env)->GetFieldID(env, clazz, "zvalAddr", "J");
    (*env)->SetLongField(env, obj, field, (jlong)0);
    zval_ptr_dtor(z);
    efree(z); // allocated in zval2obj()
}
