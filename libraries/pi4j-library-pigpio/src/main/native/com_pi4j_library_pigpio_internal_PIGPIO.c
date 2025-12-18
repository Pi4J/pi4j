/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: JNI Wrapper for PIGPIO Library
 * FILENAME      :  com_pi4j_library_pigpio_internal_PIGPIO.c
 * 
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <pigpio.h>
#include "com_pi4j_library_pigpio_internal_PIGPIO.h"


// *****************************************************************************************************
// *****************************************************************************************************
// GLOBAL TYPES/VARIABLES
// *****************************************************************************************************
// *****************************************************************************************************

/*
 * GPIO JVM instance to perform callbacks on
 */
JavaVM *callback_jvm;

/*
 * GPIO Callback Data Structure
 */
struct _PiGpioCallback
{
    jclass class;
    jmethodID method;
    jobject callback;
    jobject userdata;
};

/*
 * GPIO Alert Callback Cache for user GPIO pins (0-31) <TOTAL 32>
 */
struct _PiGpioCallback gpioAlertCallbacks[PI_MAX_USER_GPIO+1];


/*
 * GPIO ISR Callback Cache for GPIO pins (0-53) <TOTAL 54>
 */
struct _PiGpioCallback gpioIsrCallbacks[PI_MAX_GPIO+1];

/*
 * Event Callback Cache for Generic Events (0-31) <TOTAL 32>
 */
struct _PiGpioCallback eventCallbacks[PI_MAX_EVENT+1];

/*
 * Signal Callback Cache for System Signal Events (0-63) <TOTAL 64>
 */
struct _PiGpioCallback signalCallbacks[PI_MAX_SIGNUM+1];


// *****************************************************************************************************
// *****************************************************************************************************
// JNI LOAD/UNLOAD IMPLEMENTATION
// *****************************************************************************************************
// *****************************************************************************************************

/**
 * --------------------------------------------------------
 * JNI LIBRARY LOADED
 * --------------------------------------------------------
 * capture java references to be used later for callback methods
 */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved)
{
	JNIEnv *env;

	// ensure that the calling environment is a supported JNI version
    if ((*jvm)->GetEnv(jvm, (void **)&env, JNI_VERSION_1_2))
    {
    	// JNI version not supported
    	printf("NATIVE (JNI::OnLoad) ERROR; JNI version not supported.\n");
        return JNI_ERR;
    }

    // cache global reference to JVM
    callback_jvm = jvm;

	// return JNI version; success
	return JNI_VERSION_1_2;
}

/**
 * --------------------------------------------------------
 * JNI LIBRARY UNLOADED
 * --------------------------------------------------------
 * stop all monitoring threads and clean up references
 */
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *jvm, void *reserved)
{
	return;
}

// *****************************************************************************************************
// *****************************************************************************************************
// LIFECYCLE & VERSIONS IMPLEMENTATION
// *****************************************************************************************************
// *****************************************************************************************************

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioVersion
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioVersion
  (JNIEnv *env, jclass class)
{
    return gpioVersion();
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioHardwareRevision
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioHardwareRevision
  (JNIEnv *env, jclass class)
{
    return gpioHardwareRevision();
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioInitialise
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioInitialise
  (JNIEnv *env, jclass class)
{
    // SEE: https://github.com/Pi4J/pi4j/issues/15
    //  By default, PIGPIO steals all the signal handlers. The JVM doesn't like this
    //  as it uses them in running the JVM so it ends up with the program crashing.
    //  The following should disable the signal handlers inside the PIGPIO library.
    gpioCfgSetInternals (gpioCfgGetInternals () | PI_CFG_NOSIGHANDLER);

    // perform initialization of the PIGPIO library
    return gpioInitialise();
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioTerminate
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioTerminate
  (JNIEnv *env, jclass class)
{
    int gpio, event, signum;

    // before shutting down, lets clean up a bit
    for(gpio = PI_MIN_GPIO; gpio <= PI_MAX_GPIO; gpio++){

        // -------------------------
        // ALERT CALLBACKS
        // -------------------------

        if(gpio <= PI_MAX_USER_GPIO){
            // destroy any global references to the callback object instance
            if(gpioAlertCallbacks[gpio].callback != NULL){
                (*env)->DeleteGlobalRef(env, gpioAlertCallbacks[gpio].callback);
            }

            // destroy any global references to the callback userdata instance
            if(gpioAlertCallbacks[gpio].userdata != NULL){
                (*env)->DeleteGlobalRef(env, gpioAlertCallbacks[gpio].userdata);
            }

            // clear the cached callback references for this gpio pin
            gpioAlertCallbacks[gpio].callback = NULL;
            gpioAlertCallbacks[gpio].class = NULL;
            gpioAlertCallbacks[gpio].method = 0;
            gpioAlertCallbacks[gpio].userdata = NULL;
        }

        // -------------------------
        // ISR CALLBACKS
        // -------------------------

        // destroy any global references to the callback object instance
        if(gpioIsrCallbacks[gpio].callback != NULL){
            (*env)->DeleteGlobalRef(env, gpioIsrCallbacks[gpio].callback);
        }

        // destroy any global references to the callback userdata instance
        if(gpioIsrCallbacks[gpio].userdata != NULL){
            (*env)->DeleteGlobalRef(env, gpioIsrCallbacks[gpio].userdata);
        }

        // clear the cached callback references for this gpio pin
        gpioIsrCallbacks[gpio].callback = NULL;
        gpioIsrCallbacks[gpio].class = NULL;
        gpioIsrCallbacks[gpio].method = 0;
        gpioIsrCallbacks[gpio].userdata = NULL;
    }

    // -------------------------
    // EVENT CALLBACKS
    // -------------------------
    for(event = 0; event <= PI_MAX_EVENT; event++){

        // destroy any global references to the callback object instance
        if(eventCallbacks[event].callback != NULL){
            (*env)->DeleteGlobalRef(env, eventCallbacks[event].callback);
        }

        // destroy any global references to the callback userdata instance
        if(eventCallbacks[event].userdata != NULL){
            (*env)->DeleteGlobalRef(env, eventCallbacks[event].userdata);
        }

        // clear the cached callback references for this event number
        eventCallbacks[event].callback = NULL;
        eventCallbacks[event].class = NULL;
        eventCallbacks[event].method = 0;
        eventCallbacks[event].userdata = NULL;
    }

    // -------------------------
    // SIGNAL CALLBACKS
    // -------------------------
    for(signum = 0; signum <= PI_MAX_SIGNUM; signum++){

        // destroy any global references to the callback object instance
        if(signalCallbacks[signum].callback != NULL){
            (*env)->DeleteGlobalRef(env, signalCallbacks[signum].callback);
        }

        // destroy any global references to the callback userdata instance
        if(signalCallbacks[signum].userdata != NULL){
            (*env)->DeleteGlobalRef(env, signalCallbacks[signum].userdata);
        }

        // clear the cached callback references for this event number
        signalCallbacks[signum].callback = NULL;
        signalCallbacks[signum].class = NULL;
        signalCallbacks[signum].method = 0;
        signalCallbacks[signum].userdata = NULL;
    }


    return gpioTerminate();
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioCfgClock
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioCfgClock
  (JNIEnv * env, jclass class, jint micros, jint peripheral, jint source)
{
  return gpioCfgClock((unsigned)micros, (unsigned)peripheral, (unsigned)source);
}


// *****************************************************************************************************
// *****************************************************************************************************
// SIGNAL HANDLING (and callback events) IMPLEMENTATION
// *****************************************************************************************************
// *****************************************************************************************************

void signalCallbackDelegate(int signum)
{
    // attached to JVM thread
    JNIEnv *env;
    (*callback_jvm)->AttachCurrentThread(callback_jvm, (void **)&env, NULL);

    // get local references for the callback class and method to invoke based on the GPIO pin number
    jclass callback_class = signalCallbacks[signum].class;
    jmethodID callback_method = signalCallbacks[signum].method;
    jobject callback_userdata = signalCallbacks[signum].userdata;

    // ensure that the JVM exists
    if(callback_jvm == NULL){
        printf("NATIVE (PIGPIO::signalCallbackDelegate) ERROR; 'callback_jvm' is NULL.\n");
        return;
    }

    // ensure the callback class is available
    if (callback_class == NULL){
        printf("NATIVE (PIGPIO::signalCallbackDelegate) ERROR; 'callback_class' is NULL.\n");
        return;
    }

    // ensure the callback method is available
    if (callback_method == NULL){
        printf("NATIVE (PIGPIO::signalCallbackDelegate) ERROR; 'callback_class::method' is NULL.\n");
        return;
    }

    // clear any exceptions on the stack
    (*env)->ExceptionClear(env);

    // invoke callback to java state method to notify event listeners
    if(callback_userdata == NULL){
        (*env)->CallVoidMethod(env, signalCallbacks[signum].callback, callback_method, (jint)signum);
    }
    else{
        (*env)->CallVoidMethod(env, signalCallbacks[signum].callback, callback_method, (jint)signum, callback_userdata);
    }

    // clear any user caused exceptions on the stack
    if((*env)->ExceptionCheck(env)){
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
    }

    // detach from thread
    (*callback_jvm)->DetachCurrentThread(callback_jvm);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioSetSignalFunc
 * Signature: (ILcom/pi4j/library/pigpio/internal/PiGpioSignalCallback;)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioSetSignalFunc
  (JNIEnv *env, jclass class, jint signum, jobject callback)
{
    // validate the user requested signal number
    if(signum > PI_MAX_SIGNUM){
        printf("NATIVE (PIGPIO::gpioSetSignalFunc) ERROR; INVALID SIGNAL; SUPPORTED SIGNUMS: <0-63>;\n");
        return PI_BAD_SIGNUM;
    }

    jint result;
    if(callback == NULL){
        // unregister any callbacks for this signal number with the PIGPIO lib
        result = gpioSetSignalFunc((unsigned)signum, NULL);

        // destroy any global references to the callback object instance
        if(signalCallbacks[signum].callback != NULL){
            (*env)->DeleteGlobalRef(env, signalCallbacks[signum].callback);
        }

        // destroy any global references to the callback userdata instance
        if(signalCallbacks[signum].userdata != NULL){
            (*env)->DeleteGlobalRef(env, signalCallbacks[signum].userdata);
        }

        // clear the cached callback references for this signal number
        signalCallbacks[signum].callback = NULL;
        signalCallbacks[signum].class = NULL;
        signalCallbacks[signum].method = 0;
        signalCallbacks[signum].userdata = NULL;
    }
    else{
        // convert local to global reference (local will die after this method call)
        jobject callback_object = (*env)->NewGlobalRef(env, callback);

        // get the callback class; this will help us lookup the required callback method id
        jclass callback_class = (*env)->GetObjectClass(env, callback_object);
        if (callback_class == NULL){
            printf("NATIVE (PIGPIO::gpioSetSignalFunc) ERROR; Java class reference is NULL.\n");
            return JNI_ERR;
        }

        // get callable method on callback class of this callback object instance
        jmethodID callback_method = (*env)->GetMethodID(env, callback_class, "call", "(I)V");

        // cache references to the callback instance, class and method
        signalCallbacks[signum].class = callback_class;
        signalCallbacks[signum].method = callback_method;
        signalCallbacks[signum].callback = callback_object;
        signalCallbacks[signum].userdata = NULL;

        // now register the native delegate callback with PIGPIO lib
        result = gpioSetSignalFunc((unsigned)signum, signalCallbackDelegate);
    }
    return result;
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioSetSignalFuncEx
 * Signature: (ILcom/pi4j/library/pigpio/internal/PiGpioSignalCallbackEx;Ljava/lang/Object;)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioSetSignalFuncEx
  (JNIEnv *env, jclass class, jint signum, jobject callback, jobject userdata)
{
    // validate the user requested signal number
    if(signum > PI_MAX_SIGNUM){
        printf("NATIVE (PIGPIO::gpioSetSignalFuncEx) ERROR; INVALID SIGNAL; SUPPORTED SIGNUMS: <0-63>;\n");
        return PI_BAD_SIGNUM;
    }

    jint result;
    if(callback == NULL){
        // unregister any callbacks for this signal number with the PIGPIO lib
        result = gpioSetSignalFunc((unsigned)signum, NULL);

        // destroy any global references to the callback object instance
        if(signalCallbacks[signum].callback != NULL){
            (*env)->DeleteGlobalRef(env, signalCallbacks[signum].callback);
        }

        // destroy any global references to the callback userdata instance
        if(signalCallbacks[signum].userdata != NULL){
            (*env)->DeleteGlobalRef(env, signalCallbacks[signum].userdata);
        }

        // clear the cached callback references for this signal number
        signalCallbacks[signum].callback = NULL;
        signalCallbacks[signum].class = NULL;
        signalCallbacks[signum].method = 0;
        signalCallbacks[signum].userdata = NULL;
    }
    else{
        // convert local to global reference (local will die after this method call)
        jobject callback_object = (*env)->NewGlobalRef(env, callback);
        jobject callback_userdata = (*env)->NewGlobalRef(env, userdata);

        // get the callback class; this will help us lookup the required callback method id
        jclass callback_class = (*env)->GetObjectClass(env, callback_object);
        if (callback_class == NULL){
            printf("NATIVE (PIGPIO::gpioSetAlertFuncEx) ERROR; Java class reference is NULL.\n");
            return JNI_ERR;
        }

        // get callable method on callback class of this callback object instance
        jmethodID callback_method = (*env)->GetMethodID(env, callback_class, "call", "(ILjava/lang/Object;)V");

        // cache references to the callback instance, class and method
        signalCallbacks[signum].class = callback_class;
        signalCallbacks[signum].method = callback_method;
        signalCallbacks[signum].callback = callback_object;
        signalCallbacks[signum].userdata = callback_userdata;

        // now register the native delegate callback with PIGPIO lib
        result = gpioSetSignalFunc((unsigned)signum, signalCallbackDelegate);
    }
    return result;
}

  
// *****************************************************************************************************
// *****************************************************************************************************
// GPIO IMPLEMENTATION
// *****************************************************************************************************
// *****************************************************************************************************

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioSetMode
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioSetMode
  (JNIEnv *env, jclass class, jint gpio, jint mode)
{
    return gpioSetMode((unsigned)gpio, (unsigned)mode);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioGetMode
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioGetMode
  (JNIEnv *env, jclass class, jint gpio)
{
    return gpioGetMode((unsigned)gpio);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioSetPullUpDown
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioSetPullUpDown
  (JNIEnv *env, jclass class, jint gpio, jint pud)
{
    return gpioSetPullUpDown((unsigned)gpio, (unsigned)pud);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioRead
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioRead
  (JNIEnv *env, jclass class, jint gpio)
{
    return gpioRead((unsigned)gpio);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioWrite
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioWrite
  (JNIEnv *env, jclass class, jint gpio, jint level)
{
    return gpioWrite((unsigned)gpio, (unsigned)level);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioTrigger
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioTrigger
  (JNIEnv *env, jclass class, jint user_gpio, jint pulseLen, jint level)
{
    return gpioTrigger((unsigned)user_gpio, (unsigned)pulseLen, (unsigned)level);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioSetWatchdog
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioSetWatchdog
  (JNIEnv *env, jclass class, jint user_gpio, jint timeout)
{
    return gpioSetWatchdog((unsigned)user_gpio, (unsigned)timeout);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioNoiseFilter
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioNoiseFilter
  (JNIEnv *env, jclass class, jint user_gpio, jint steady, jint active)
{
    return gpioNoiseFilter((unsigned)user_gpio, (unsigned)steady, (unsigned)active);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioGlitchFilter
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioGlitchFilter
  (JNIEnv *env, jclass class, jint user_gpio, jint steady)
{
    return gpioGlitchFilter((unsigned)user_gpio, (unsigned)steady);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioGetPad
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioGetPad
  (JNIEnv *env, jclass class, jint pad)
{
    return gpioGetPad((unsigned)pad);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioSetPad
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioSetPad
  (JNIEnv *env, jclass class, jint pad, jint padStrength)
{
    return gpioSetPad((unsigned)pad, (unsigned)padStrength);
}


// *****************************************************************************************************
// *****************************************************************************************************
// GPIO PIN NOTIFICATIONS (PIPE & SOCKET)
// *****************************************************************************************************
// *****************************************************************************************************

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioNotifyOpen
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioNotifyOpen
  (JNIEnv *env , jclass class)
{
    return gpioNotifyOpen();
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioNotifyOpenWithSize
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioNotifyOpenWithSize
  (JNIEnv *env, jclass class, jint bufSize)
{
    return gpioNotifyOpenWithSize(bufSize);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioNotifyBegin
 * Signature: (IJ)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioNotifyBegin
  (JNIEnv *env, jclass class, jint handle, jlong bits)
{
    return gpioNotifyBegin((unsigned)handle, (uint32_t)bits);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioNotifyPause
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioNotifyPause
  (JNIEnv *env, jclass class, jint handle)
{
    return gpioNotifyPause((unsigned)handle);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioNotifyClose
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioNotifyClose
  (JNIEnv *env, jclass class, jint handle)
{
    return gpioNotifyClose((unsigned)handle);
}

// *****************************************************************************************************
// *****************************************************************************************************
// GPIO ALERTS (and callbacks) IMPLEMENTATION
// *****************************************************************************************************
// *****************************************************************************************************

void gpioAlertCallbackDelegate(int gpio, int level, uint32_t tick)
{
    // attached to JVM thread
    JNIEnv *env;
    (*callback_jvm)->AttachCurrentThread(callback_jvm, (void **)&env, NULL);

    // get local references for the callback class and method to invoke based on the GPIO pin number
    jclass callback_class = gpioAlertCallbacks[gpio].class;
    jmethodID callback_method = gpioAlertCallbacks[gpio].method;
    jobject callback_userdata = gpioAlertCallbacks[gpio].userdata;

    // ensure that the JVM exists
    if(callback_jvm == NULL){
        printf("NATIVE (PIGPIO::gpioAlertCallbackDelegate) ERROR; CallbackWrapperFunc 'callback_jvm' is NULL.\n");
        return;
    }

    // ensure the callback class is available
    if (callback_class == NULL){
        printf("NATIVE (PIGPIO::gpioAlertCallbackDelegate) ERROR; CallbackWrapperFunc 'callback_class' is NULL.\n");
        return;
    }

    // ensure the callback method is available
    if (callback_method == NULL){
        printf("NATIVE (PIGPIO::gpioAlertCallbackDelegate) ERROR; CallbackWrapperFunc 'callback_class::method' is NULL.\n");
        return;
    }

    // clear any exceptions on the stack
    (*env)->ExceptionClear(env);

    // invoke callback to java state method to notify event listeners
    if(callback_userdata == NULL){
        (*env)->CallVoidMethod(env, gpioAlertCallbacks[gpio].callback, callback_method, (jint)gpio, (jint)level, (jlong)tick);
    }
    else{
        (*env)->CallVoidMethod(env, gpioAlertCallbacks[gpio].callback, callback_method, (jint)gpio, (jint)level, (jlong)tick, callback_userdata);
    }

    // clear any user caused exceptions on the stack
    if((*env)->ExceptionCheck(env)){
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
    }

    // detach from thread
    (*callback_jvm)->DetachCurrentThread(callback_jvm);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioSetAlertFunc
 * Signature: (ILcom/pi4j/library/pigpio/internal/PiGpioAlertCallback;)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioSetAlertFunc
  (JNIEnv *env, jclass class, jint user_gpio, jobject callback)
{
    // validate the user requested GPIO pin
    if(user_gpio > PI_MAX_USER_GPIO){
        printf("NATIVE (PIGPIO::gpioSetAlertFunc) ERROR; INVALID GPIO PIN; SUPPORTED PINS: <0-31>;\n");
        return PI_BAD_USER_GPIO;
    }

    jint result;
    if(callback == NULL){
        // unregister any callbacks for this gpio pin with the PIGPIO lib
        result = gpioSetAlertFunc((unsigned)user_gpio, NULL);

        // destroy any global references to the callback object instance
        if(gpioAlertCallbacks[user_gpio].callback != NULL){
            (*env)->DeleteGlobalRef(env, gpioAlertCallbacks[user_gpio].callback);
        }

        // destroy any global references to the callback userdata instance
        if(gpioAlertCallbacks[user_gpio].userdata != NULL){
            (*env)->DeleteGlobalRef(env, gpioAlertCallbacks[user_gpio].userdata);
        }

        // clear the cached callback references for this gpio pin
        gpioAlertCallbacks[user_gpio].callback = NULL;
        gpioAlertCallbacks[user_gpio].class = NULL;
        gpioAlertCallbacks[user_gpio].method = 0;
        gpioAlertCallbacks[user_gpio].userdata = NULL;
    }
    else{
        // convert local to global reference (local will die after this method call)
        jobject callback_object = (*env)->NewGlobalRef(env, callback);

        // get the callback class; this will help us lookup the required callback method id
        jclass callback_class = (*env)->GetObjectClass(env, callback_object);
        if (callback_class == NULL){
            printf("NATIVE (PIGPIO::gpioSetAlertFunc) ERROR; Java class reference is NULL.\n");
            return JNI_ERR;
        }

        // get callable method on callback class of this callback object instance
        jmethodID callback_method = (*env)->GetMethodID(env, callback_class, "call", "(IIJ)V");

        // cache references to the callback instance, class and method
        gpioAlertCallbacks[user_gpio].class = callback_class;
        gpioAlertCallbacks[user_gpio].method = callback_method;
        gpioAlertCallbacks[user_gpio].callback = callback_object;
        gpioAlertCallbacks[user_gpio].userdata = NULL;

        // now register the native delegate callback with PIGPIO lib
        result = gpioSetAlertFunc((unsigned)user_gpio, gpioAlertCallbackDelegate);
    }
    return result;
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioSetAlertFuncEx
 * Signature: (ILcom/pi4j/library/pigpio/internal/PiGpioAlertCallbackEx;Ljava/lang/Object;)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioSetAlertFuncEx
  (JNIEnv *env, jclass class, jint user_gpio, jobject callback, jobject userdata)
{
    // validate the user requested GPIO pin
    if(user_gpio > PI_MAX_USER_GPIO){
        printf("NATIVE (PIGPIO::gpioSetAlertFuncEx) ERROR; INVALID GPIO PIN; SUPPORTED PINS: <0-31>;\n");
        return PI_BAD_USER_GPIO;
    }

    jint result;
    if(callback == NULL){
        // unregister any callbacks for this gpio pin with the PIGPIO lib
        result = gpioSetAlertFunc((unsigned)user_gpio, NULL);

        // destroy any global references to the callback object instance
        if(gpioAlertCallbacks[user_gpio].callback != NULL){
            (*env)->DeleteGlobalRef(env, gpioAlertCallbacks[user_gpio].callback);
        }

        // destroy any global references to the callback userdata instance
        if(gpioAlertCallbacks[user_gpio].userdata != NULL){
            (*env)->DeleteGlobalRef(env, gpioAlertCallbacks[user_gpio].userdata);
        }

        // clear the cached callback references for this gpio pin
        gpioAlertCallbacks[user_gpio].callback = NULL;
        gpioAlertCallbacks[user_gpio].class = NULL;
        gpioAlertCallbacks[user_gpio].method = 0;
        gpioAlertCallbacks[user_gpio].userdata = NULL;
    }
    else{
        // convert local to global reference (local will die after this method call)
        jobject callback_object = (*env)->NewGlobalRef(env, callback);
        jobject callback_userdata = (*env)->NewGlobalRef(env, userdata);

        // get the callback class; this will help us lookup the required callback method id
        jclass callback_class = (*env)->GetObjectClass(env, callback_object);
        if (callback_class == NULL){
            printf("NATIVE (PIGPIO::gpioSetAlertFuncEx) ERROR; Java class reference is NULL.\n");
            return JNI_ERR;
        }

        // get callable method on callback class of this callback object instance
        jmethodID callback_method = (*env)->GetMethodID(env, callback_class, "call", "(IIJLjava/lang/Object;)V");

        // cache references to the callback instance, class and method
        gpioAlertCallbacks[user_gpio].class = callback_class;
        gpioAlertCallbacks[user_gpio].method = callback_method;
        gpioAlertCallbacks[user_gpio].callback = callback_object;
        gpioAlertCallbacks[user_gpio].userdata = callback_userdata;

        // now register the native delegate callback with PIGPIO lib
        result = gpioSetAlertFunc((unsigned)user_gpio, gpioAlertCallbackDelegate);
    }
    return result;
}

// *****************************************************************************************************
// *****************************************************************************************************
// GPIO INTERRUPTS (and callbacks) IMPLEMENTATION
// *****************************************************************************************************
// *****************************************************************************************************

void gpioIsrCallbackDelegate(int gpio, int level, uint32_t tick)
{
    // attached to JVM thread
    JNIEnv *env;
    (*callback_jvm)->AttachCurrentThread(callback_jvm, (void **)&env, NULL);

    // get local references for the callback class and method to invoke based on the GPIO pin number
    jclass callback_class = gpioIsrCallbacks[gpio].class;
    jmethodID callback_method = gpioIsrCallbacks[gpio].method;
    jobject callback_userdata = gpioIsrCallbacks[gpio].userdata;

    // ensure that the JVM exists
    if(callback_jvm == NULL){
        printf("NATIVE (PIGPIO::gpioIsrCallbackDelegate) ERROR; CallbackWrapperFunc 'callback_jvm' is NULL.\n");
        return;
    }

    // ensure the callback class is available
    if (callback_class == NULL){
        printf("NATIVE (PIGPIO::gpioIsrCallbackDelegate) ERROR; CallbackWrapperFunc 'callback_class' is NULL.\n");
        return;
    }

    // ensure the callback method is available
    if (callback_method == NULL){
        printf("NATIVE (PIGPIO::gpioIsrCallbackDelegate) ERROR; CallbackWrapperFunc 'callback_class::method' is NULL.\n");
        return;
    }

    // clear any exceptions on the stack
    (*env)->ExceptionClear(env);

    // invoke callback to java state method to notify event listeners
    if(callback_userdata == NULL){
        (*env)->CallVoidMethod(env, gpioIsrCallbacks[gpio].callback, callback_method, (jint)gpio, (jint)level, (jlong)tick);
    }
    else{
        (*env)->CallVoidMethod(env, gpioIsrCallbacks[gpio].callback, callback_method, (jint)gpio, (jint)level, (jlong)tick, callback_userdata);
    }

    // clear any user caused exceptions on the stack
    if((*env)->ExceptionCheck(env)){
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
    }

    // detach from thread
    (*callback_jvm)->DetachCurrentThread(callback_jvm);
}


/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioSetISRFunc
 * Signature: (IIILcom/pi4j/library/pigpio/internal/PiGpioAlertCallback;)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioSetISRFunc
  (JNIEnv *env, jclass class, jint gpio, jint edge, jint timeout, jobject callback)
{
    // validate the user requested GPIO pin
    if(gpio > PI_MAX_GPIO){
        printf("NATIVE (PIGPIO::gpioSetISRFunc) ERROR; INVALID GPIO PIN; SUPPORTED PINS: <0-53>;\n");
        return PI_BAD_GPIO;
    }

    jint result;
    if(callback == NULL){
        // unregister any callbacks for this gpio pin with the PIGPIO lib
        result = gpioSetISRFunc((unsigned)gpio, (unsigned)edge, (unsigned)time, NULL);

        // destroy any global references to the callback object instance
        if(gpioIsrCallbacks[gpio].callback != NULL){
            (*env)->DeleteGlobalRef(env, gpioIsrCallbacks[gpio].callback);
        }

        // destroy any global references to the callback userdata instance
        if(gpioIsrCallbacks[gpio].userdata != NULL){
            (*env)->DeleteGlobalRef(env, gpioIsrCallbacks[gpio].userdata);
        }

        // clear the cached callback references for this gpio pin
        gpioIsrCallbacks[gpio].callback = NULL;
        gpioIsrCallbacks[gpio].class = NULL;
        gpioIsrCallbacks[gpio].method = 0;
        gpioIsrCallbacks[gpio].userdata = NULL;
    }
    else{
        // convert local to global reference (local will die after this method call)
        jobject callback_object = (*env)->NewGlobalRef(env, callback);

        // get the callback class; this will help us lookup the required callback method id
        jclass callback_class = (*env)->GetObjectClass(env, callback_object);
        if (callback_class == NULL){
            printf("NATIVE (PIGPIO::gpioSetISRFunc) ERROR; Java class reference is NULL.\n");
            return JNI_ERR;
        }

        // get callable method on callback class of this callback object instance
        jmethodID callback_method = (*env)->GetMethodID(env, callback_class, "call", "(IIJ)V");

        // cache references to the callback instance, class and method
        gpioIsrCallbacks[gpio].class = callback_class;
        gpioIsrCallbacks[gpio].method = callback_method;
        gpioIsrCallbacks[gpio].callback = callback_object;
        gpioIsrCallbacks[gpio].userdata = NULL;

        // now register the native delegate callback with PIGPIO lib
        result = gpioSetISRFunc((unsigned)gpio, (unsigned)edge, (unsigned)timeout, gpioIsrCallbackDelegate);
    }
    return result;
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioSetISRFuncEx
 * Signature: (IIILcom/pi4j/library/pigpio/internal/PiGpioAlertCallbackEx;Ljava/lang/Object;)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioSetISRFuncEx
  (JNIEnv *env, jclass class, jint gpio, jint edge, jint timeout, jobject callback, jobject userdata)
{
    // validate the user requested GPIO pin
    if(gpio > PI_MAX_GPIO){
        printf("NATIVE (PIGPIO::gpioSetISRFuncEx) ERROR; INVALID GPIO PIN; SUPPORTED PINS: <0-53>;\n");
        return PI_BAD_GPIO;
    }

    jint result;
    if(callback == NULL){
        // unregister any callbacks for this gpio pin with the PIGPIO lib
        result = gpioSetISRFunc((unsigned)gpio, (unsigned)edge, (unsigned)time, NULL);

        // destroy any global references to the callback object instance
        if(gpioIsrCallbacks[gpio].callback != NULL){
            (*env)->DeleteGlobalRef(env, gpioIsrCallbacks[gpio].callback);
        }

        // destroy any global references to the callback userdata instance
        if(gpioIsrCallbacks[gpio].userdata != NULL){
            (*env)->DeleteGlobalRef(env, gpioIsrCallbacks[gpio].userdata);
        }

        // clear the cached callback references for this gpio pin
        gpioIsrCallbacks[gpio].callback = NULL;
        gpioIsrCallbacks[gpio].class = NULL;
        gpioIsrCallbacks[gpio].method = 0;
        gpioIsrCallbacks[gpio].userdata = NULL;
    }
    else{
        // convert local to global reference (local will die after this method call)
        jobject callback_object = (*env)->NewGlobalRef(env, callback);
        jobject callback_userdata = (*env)->NewGlobalRef(env, userdata);

        // get the callback class; this will help us lookup the required callback method id
        jclass callback_class = (*env)->GetObjectClass(env, callback_object);
        if (callback_class == NULL){
            printf("NATIVE (PIGPIO::gpioSetISRFuncEx) ERROR; Java class reference is NULL.\n");
            return JNI_ERR;
        }

        // get callable method on callback class of this callback object instance
        jmethodID callback_method = (*env)->GetMethodID(env, callback_class, "call", "(IIJLjava/lang/Object;)V");

        // cache references to the callback instance, class and method
        gpioIsrCallbacks[gpio].class = callback_class;
        gpioIsrCallbacks[gpio].method = callback_method;
        gpioIsrCallbacks[gpio].callback = callback_object;
        gpioIsrCallbacks[gpio].userdata = callback_userdata;

        // now register the native delegate callback with PIGPIO lib
        result = gpioSetISRFunc((unsigned)gpio, (unsigned)edge, (unsigned)timeout, gpioIsrCallbackDelegate);
    }
    return result;
}


// *****************************************************************************************************
// *****************************************************************************************************
// GENERIC EVENTS (and callbacks) IMPLEMENTATION
// *****************************************************************************************************
// *****************************************************************************************************

void eventCallbackDelegate(int event, uint32_t tick)
{
    // attached to JVM thread
    JNIEnv *env;
    (*callback_jvm)->AttachCurrentThread(callback_jvm, (void **)&env, NULL);

    // get local references for the callback class and method to invoke based on the event ID
    jclass callback_class = eventCallbacks[event].class;
    jmethodID callback_method = eventCallbacks[event].method;
    jobject callback_userdata = eventCallbacks[event].userdata;

    // ensure that the JVM exists
    if(callback_jvm == NULL){
        printf("NATIVE (PIGPIO::eventCallbackDelegate) ERROR; 'callback_jvm' is NULL.\n");
        return;
    }

    // ensure the callback class is available
    if (callback_class == NULL){
        printf("NATIVE (PIGPIO::eventCallbackDelegate) ERROR; 'callback_class' is NULL.\n");
        return;
    }

    // ensure the callback method is available
    if (callback_method == NULL){
        printf("NATIVE (PIGPIO::eventCallbackDelegate) ERROR; 'callback_class::method' is NULL.\n");
        return;
    }

    // clear any exceptions on the stack
    (*env)->ExceptionClear(env);

    // invoke callback to java state method to notify event listeners
    if(callback_userdata == NULL){
        (*env)->CallVoidMethod(env, eventCallbacks[event].callback, callback_method, (jint)event, (jlong)tick);
    }
    else{
        (*env)->CallVoidMethod(env, eventCallbacks[event].callback, callback_method, (jint)event, (jlong)tick, callback_userdata);
    }

    // clear any user caused exceptions on the stack
    if((*env)->ExceptionCheck(env)){
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
    }

    // detach from thread
    (*callback_jvm)->DetachCurrentThread(callback_jvm);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    eventSetFunc
 * Signature: (ILcom/pi4j/library/pigpio/internal/PiGpioEventCallback;)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_eventSetFunc
  (JNIEnv *env, jclass class, jint event, jobject callback)
{
    // validate the user requested event id
    if(event > PI_MAX_EVENT){
        printf("NATIVE (PIGPIO::eventSetFunc) ERROR; INVALID EVENT ID; SUPPORTED EVENTS: <0-31>;\n");
        return PI_BAD_EVENT_ID;
    }

    jint result;
    if(callback == NULL){
        // unregister any callbacks for this gpio pin with the PIGPIO lib
        result = eventSetFunc((unsigned)event, NULL);

        // destroy any global references to the callback object instance
        if(eventCallbacks[event].callback != NULL){
            (*env)->DeleteGlobalRef(env, eventCallbacks[event].callback);
        }

        // destroy any global references to the callback userdata instance
        if(eventCallbacks[event].userdata != NULL){
            (*env)->DeleteGlobalRef(env, eventCallbacks[event].userdata);
        }

        // clear the cached callback references for this event ID
        eventCallbacks[event].callback = NULL;
        eventCallbacks[event].class = NULL;
        eventCallbacks[event].method = 0;
        eventCallbacks[event].userdata = NULL;
    }
    else{
        // convert local to global reference (local will die after this method call)
        jobject callback_object = (*env)->NewGlobalRef(env, callback);

        // get the callback class; this will help us lookup the required callback method id
        jclass callback_class = (*env)->GetObjectClass(env, callback_object);
        if (callback_class == NULL){
            printf("NATIVE (PIGPIO::eventSetFunc) ERROR; Java class reference is NULL.\n");
            return JNI_ERR;
        }

        // get callable method on callback class of this callback object instance
        jmethodID callback_method = (*env)->GetMethodID(env, callback_class, "call", "(IJ)V");

        // cache references to the callback instance, class and method
        eventCallbacks[event].class = callback_class;
        eventCallbacks[event].method = callback_method;
        eventCallbacks[event].callback = callback_object;
        eventCallbacks[event].userdata = NULL;

        // now register the native delegate callback with PIGPIO lib
        result = eventSetFunc((unsigned)event, eventCallbackDelegate);
    }
    return result;
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    eventSetFuncEx
 * Signature: (ILcom/pi4j/library/pigpio/internal/PiGpioEventCallbackEx;Ljava/lang/Object;)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_eventSetFuncEx
  (JNIEnv *env, jclass class, jint event, jobject callback, jobject userdata)
{
    // validate the user requested event id
    if(event > PI_MAX_EVENT){
        printf("NATIVE (PIGPIO::eventSetFuncEx) ERROR; INVALID EVENT ID; SUPPORTED EVENTS: <0-31>;\n");
        return PI_BAD_EVENT_ID;
    }

    jint result;
    if(callback == NULL){
        // unregister any callbacks for this gpio pin with the PIGPIO lib
        result = eventSetFunc((unsigned)event, NULL);

        // destroy any global references to the callback object instance
        if(eventCallbacks[event].callback != NULL){
            (*env)->DeleteGlobalRef(env, eventCallbacks[event].callback);
        }

        // destroy any global references to the callback userdata instance
        if(eventCallbacks[event].userdata != NULL){
            (*env)->DeleteGlobalRef(env, eventCallbacks[event].userdata);
        }

        // clear the cached callback references for this event ID
        eventCallbacks[event].callback = NULL;
        eventCallbacks[event].class = NULL;
        eventCallbacks[event].method = 0;
        eventCallbacks[event].userdata = NULL;
    }
    else{
        // convert local to global reference (local will die after this method call)
        jobject callback_object = (*env)->NewGlobalRef(env, callback);
        jobject callback_userdata = (*env)->NewGlobalRef(env, userdata);

        // get the callback class; this will help us lookup the required callback method id
        jclass callback_class = (*env)->GetObjectClass(env, callback_object);
        if (callback_class == NULL){
            printf("NATIVE (PIGPIO::eventSetFuncEx) ERROR; Java class reference is NULL.\n");
            return JNI_ERR;
        }

        // get callable method on callback class of this callback object instance
        jmethodID callback_method = (*env)->GetMethodID(env, callback_class, "call", "(IJ)V");

        // cache references to the callback instance, class and method
        eventCallbacks[event].class = callback_class;
        eventCallbacks[event].method = callback_method;
        eventCallbacks[event].callback = callback_object;
        eventCallbacks[event].userdata = callback_userdata;

        // now register the native delegate callback with PIGPIO lib
        result = eventSetFunc((unsigned)event, eventCallbackDelegate);
    }
    return result;
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    eventMonitor
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_eventMonitor
  (JNIEnv *env, jclass class, jint handle, jint bits)
{
    return eventMonitor((unsigned)handle, (uint32_t)bits);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    eventTrigger
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_eventTrigger
  (JNIEnv *env, jclass class, jint event)
{
    return eventTrigger(event);
}


// *****************************************************************************************************
// *****************************************************************************************************
// PWM IMPLEMENTATION
// *****************************************************************************************************
// *****************************************************************************************************

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioPWM
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioPWM
  (JNIEnv *env, jclass class, jint user_gpio, jint dutycycle)
{
    return gpioPWM((unsigned)user_gpio, (unsigned)dutycycle);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioGetPWMdutycycle
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioGetPWMdutycycle
  (JNIEnv *env, jclass class, jint user_gpio)
{
    return gpioGetPWMdutycycle((unsigned)user_gpio);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioSetPWMrange
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioSetPWMrange
  (JNIEnv *env, jclass class, jint user_gpio, jint range)
{
    return gpioSetPWMrange((unsigned)user_gpio, (unsigned)range);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioGetPWMrange
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioGetPWMrange
  (JNIEnv *env, jclass class, jint user_gpio)
{
    return gpioGetPWMrange((unsigned)user_gpio);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioGetPWMrealRange
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioGetPWMrealRange
  (JNIEnv *env, jclass class, jint user_gpio)
{
    return gpioGetPWMrealRange((unsigned)user_gpio);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioSetPWMfrequency
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioSetPWMfrequency
  (JNIEnv *env, jclass class, jint user_gpio, jint frequency)
{
    return gpioSetPWMfrequency((unsigned)user_gpio, (unsigned)frequency);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioGetPWMfrequency
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioGetPWMfrequency
  (JNIEnv *env, jclass class, jint user_gpio)
{
    return gpioGetPWMfrequency((unsigned)user_gpio);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioHardwarePWM
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioHardwarePWM
  (JNIEnv *env, jclass class, jint user_gpio, jint frequency, jint dutyCycle)
{
    return gpioHardwarePWM((unsigned)user_gpio, (unsigned)frequency, (unsigned)dutyCycle);
}

// *****************************************************************************************************
// *****************************************************************************************************
// SERVO IMPLEMENTATION
// *****************************************************************************************************
// *****************************************************************************************************

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioServo
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioServo
  (JNIEnv *env, jclass class, jint user_gpio, jint pulsewidth)
{
    return gpioServo((unsigned)user_gpio, (unsigned)pulsewidth);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioGetServoPulsewidth
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioGetServoPulsewidth
  (JNIEnv *env, jclass class, jint user_gpio)
{
    return gpioGetServoPulsewidth((unsigned)user_gpio);
}


// *****************************************************************************************************
// *****************************************************************************************************
// I2C IMPLEMENTATION
// *****************************************************************************************************
// *****************************************************************************************************

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    i2cOpen
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_i2cOpen
  (JNIEnv *env, jclass class, jint i2CBus, jint i2cAddr, jint i2cFlags)
{
    return i2cOpen((unsigned)i2CBus, (unsigned)i2cAddr, (unsigned)i2cFlags);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    i2cClose
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_i2cClose
  (JNIEnv *env, jclass class, jint handle)
{
    return i2cClose((unsigned)handle);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    i2cWriteQuick
 * Signature: (IZ)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_i2cWriteQuick
  (JNIEnv *env, jclass class, jint handle, jboolean bit)
{
    unsigned bitVal = 0;
    if(bit) bitVal = 1;
    return i2cWriteQuick((unsigned)handle, bitVal);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    i2cWriteByte
 * Signature: (IB)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_i2cWriteByte
  (JNIEnv *env, jclass class, jint handle, jbyte bVal)
{
    return i2cWriteByte((unsigned)handle, (unsigned)bVal);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    i2cReadByte
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_i2cReadByte
  (JNIEnv *env, jclass class, jint handle)
{
    return i2cReadByte((unsigned)handle);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    i2cWriteByteData
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_i2cWriteByteData
  (JNIEnv *env, jclass class, jint handle, jint i2cReg, jint bVal)
{
    return i2cWriteByteData((unsigned)handle, (unsigned)i2cReg, (unsigned)bVal);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    i2cWriteWordData
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_i2cWriteWordData
  (JNIEnv *env, jclass class, jint handle, jint i2cReg, jint wVal)
{
    return i2cWriteWordData((unsigned)handle, (unsigned)i2cReg, (unsigned)wVal);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    i2cReadByteData
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_i2cReadByteData
  (JNIEnv *env, jclass class, jint handle, jint i2cReg)
{
    return i2cReadByteData((unsigned)handle, (unsigned)i2cReg);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    i2cReadWordData
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_i2cReadWordData
  (JNIEnv *env, jclass class, jint handle, jint i2cReg)
{
    return i2cReadWordData((unsigned)handle, (unsigned)i2cReg);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    i2cProcessCall
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_i2cProcessCall
  (JNIEnv *env, jclass class, jint handle, jint i2cReg, int wVal)
{
    return i2cProcessCall((unsigned)handle, (unsigned)i2cReg, (unsigned)wVal);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    i2cWriteBlockData
 * Signature: (II[BII)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_i2cWriteBlockData
  (JNIEnv *env, jclass class, jint handle, jint i2cReg, jbyteArray data, jint offset, jint count)
{
	// obtain a pointer to the elements of the array and pin the memory
    jbyte *buffer = (*env)->GetByteArrayElements(env, data, 0);

    // get the maximum size of the Java data array; subtract any offset value
    jsize max_length = (*env)->GetArrayLength(env, data) - offset;

    // bounds check to make sure byte count does not exceed max array length (minus offset)
    int length = (count > max_length) ? max_length : count;

    // create a new buffer pointer using the offset
    jbyte *offsetBuffer = buffer + offset;

    // perform the actual I2C write operation into the native buffer array
    jint result = i2cWriteBlockData((unsigned)handle, (unsigned)i2cReg, (char *)offsetBuffer, (unsigned)length);

	// unpin the reserved memory for 'data'; abort preserving any changes back to the Java array
	(*env)->ReleaseByteArrayElements(env, data, buffer, JNI_ABORT);

    // return the result
	return result;

}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    i2cReadBlockData
 * Signature: (II[BI)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_i2cReadBlockData
  (JNIEnv *env, jclass class, jint handle, jint i2cReg, jbyteArray data, jint offset)
{
	// obtain a pointer to the elements of the array and pin the memory
    jbyte *buffer = (*env)->GetByteArrayElements(env, data, 0);

    // create a new buffer pointer using the offset
    jbyte *offsetBuffer = buffer + offset;

    // perform the actual I2C read operation into the native buffer array
	jint result = i2cReadBlockData((unsigned)handle, (unsigned)i2cReg, (char *)offsetBuffer);

	// unpin the reserved memory for 'data'; persist change to the Java array and free the native array
	(*env)->ReleaseByteArrayElements(env, data, buffer, 0);

    // return the result
	return result;
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    i2cBlockProcessCall
 * Signature: (II[BII)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_i2cBlockProcessCall
  (JNIEnv *env, jclass class, jint handle, jint i2cReg, jbyteArray data, jint offset, jint count)
{
	// obtain a pointer to the elements of the array and pin the memory
    jbyte *buffer = (*env)->GetByteArrayElements(env, data, 0);

    // get the maximum size of the Java data array; subtract any offset value
    jsize max_length = (*env)->GetArrayLength(env, data) - offset;

    // bounds check to make sure byte count does not exceed max array length (minus offset)
    int length = (count > max_length) ? max_length : count;

    // create a new buffer pointer using the offset
    jbyte *offsetBuffer = buffer + offset;

    // perform the actual I2C read operation into the native buffer array
	jint result = i2cBlockProcessCall((unsigned)handle, (unsigned)i2cReg, (char *)offsetBuffer, (unsigned)length);

	// unpin the reserved memory for 'data'; persist change to the Java array and free the native array
	(*env)->ReleaseByteArrayElements(env, data, buffer, 0);

    // return the result
	return result;
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    i2cReadI2CBlockData
 * Signature: (II[BII)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_i2cReadI2CBlockData
  (JNIEnv *env, jclass class, jint handle, jint i2cReg, jbyteArray data, jint offset, jint count)
{
	// obtain a pointer to the elements of the array and pin the memory
    jbyte *buffer = (*env)->GetByteArrayElements(env, data, 0);

    // get the maximum size of the Java data array; subtract any offset value
    jsize max_length = (*env)->GetArrayLength(env, data) - offset;

    // bounds check to make sure byte count does not exceed max array length (minus offset)
    int length = (count > max_length) ? max_length : count;

    // create a new buffer pointer using the offset
    jbyte *offsetBuffer = buffer + offset;

    // perform the actual I2C read operation into the native buffer array
	jint result = i2cReadI2CBlockData((unsigned)handle, (unsigned)i2cReg, (char *)offsetBuffer, (unsigned)length);

	// unpin the reserved memory for 'data'; persist change to the Java array and free the native array
	(*env)->ReleaseByteArrayElements(env, data, buffer, 0);

    // return the result
	return result;
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    i2cWriteI2CBlockData
 * Signature: (II[BII)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_i2cWriteI2CBlockData
  (JNIEnv *env, jclass class, jint handle, jint i2cReg, jbyteArray data, jint offset, jint count)
{
	// obtain a pointer to the elements of the array and pin the memory
    jbyte *buffer = (*env)->GetByteArrayElements(env, data, 0);

    // get the maximum size of the Java data array; subtract any offset value
    jsize max_length = (*env)->GetArrayLength(env, data) - offset;

    // bounds check to make sure byte count does not exceed max array length (minus offset)
    int length = (count > max_length) ? max_length : count;

    // create a new buffer pointer using the offset
    jbyte *offsetBuffer = buffer + offset;

    // perform the actual I2C write operation into the native buffer array
    jint result = i2cWriteI2CBlockData((unsigned)handle, (unsigned)i2cReg, (char *)offsetBuffer, (unsigned)length);

	// unpin the reserved memory for 'data'; abort preserving any changes back to the Java array
	(*env)->ReleaseByteArrayElements(env, data, buffer, JNI_ABORT);

    // return the result
	return result;
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    i2cReadDevice
 * Signature: (I[BII)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_i2cReadDevice
  (JNIEnv *env, jclass class, jint handle, jbyteArray data, jint offset, jint count)
{
	// obtain a pointer to the elements of the array and pin the memory
    jbyte *buffer = (*env)->GetByteArrayElements(env, data, 0);

    // get the maximum size of the Java data array; subtract any offset value
    jsize max_length = (*env)->GetArrayLength(env, data) - offset;

    // bounds check to make sure byte count does not exceed max array length (minus offset)
    int length = (count > max_length) ? max_length : count;

    // create a new buffer pointer using the offset
    jbyte *offsetBuffer = buffer + offset;

    // perform the actual I2C read operation into the native buffer array
	jint result = i2cReadDevice((unsigned)handle, (char *)offsetBuffer, (unsigned)length);

	// unpin the reserved memory for 'data'; persist change to the Java array and free the native array
	(*env)->ReleaseByteArrayElements(env, data, buffer, 0);

    // return the result
	return result;
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    i2cWriteDevice
 * Signature: (I[BII)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_i2cWriteDevice
  (JNIEnv *env, jclass class, jint handle, jbyteArray data, jint offset, jint count)
{
	// obtain a pointer to the elements of the array and pin the memory
    jbyte *buffer = (*env)->GetByteArrayElements(env, data, 0);

    // get the maximum size of the Java data array; subtract any offset value
    jsize max_length = (*env)->GetArrayLength(env, data) - offset;

    // bounds check to make sure byte count does not exceed max array length (minus offset)
    int length = (count > max_length) ? max_length : count;

    // create a new buffer pointer using the offset
    jbyte *offsetBuffer = buffer + offset;

    // perform the actual I2C write operation into the native buffer array
    jint result = i2cWriteDevice((unsigned)handle, (char *)offsetBuffer, (unsigned)length);

	// unpin the reserved memory for 'data'; abort preserving any changes back to the Java array
	(*env)->ReleaseByteArrayElements(env, data, buffer, JNI_ABORT);

    // return the result
	return result;
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    i2cSwitchCombined
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_i2cSwitchCombined
  (JNIEnv *env, jclass class, jint setting)
{
    i2cSwitchCombined(setting);
}

// *****************************************************************************************************
// *****************************************************************************************************
// SPI IMPLEMENTATION
// *****************************************************************************************************
// *****************************************************************************************************

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    spiOpen
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_spiOpen
  (JNIEnv *env, jclass class, jint spiChan, jint baud, jint spiFlags)
{
    return spiOpen((unsigned)spiChan, (unsigned)baud, (unsigned)spiFlags);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    spiClose
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_spiClose
  (JNIEnv *env, jclass class, jint handle)
{
    return spiClose((unsigned)handle);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    spiRead
 * Signature: (I[BII)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_spiRead
  (JNIEnv *env, jclass class, jint handle, jbyteArray data, jint offset, jint count)
{
	// obtain a pointer to the elements of the array and pin the memory
    jbyte *buffer = (*env)->GetByteArrayElements(env, data, 0);

    // get the maximum size of the Java data array; subtract any offset value
    jsize max_length = (*env)->GetArrayLength(env, data) - offset;

    // bounds check to make sure byte count does not exceed max array length (minus offset)
    int length = (count > max_length) ? max_length : count;

    // create a new buffer pointer using the offset
    jbyte *offsetBuffer = buffer + offset;

    // perform the actual SPI read operation into the native buffer array
	jint result = spiRead((unsigned)handle, (char *)offsetBuffer, (unsigned)length);

	// unpin the reserved memory for 'data'; persist change to the Java array and free the native array
	(*env)->ReleaseByteArrayElements(env, data, buffer, 0);

    // return the result
	return result;
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    spiWrite
 * Signature: (I[BII)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_spiWrite
  (JNIEnv *env, jclass class, jint handle, jbyteArray data, jint offset, jint count)
{
	// obtain a pointer to the elements of the array and pin the memory
    jbyte *buffer = (*env)->GetByteArrayElements(env, data, 0);

    // get the maximum size of the Java data array; subtract any offset value
    jsize max_length = (*env)->GetArrayLength(env, data) - offset;

    // bounds check to make sure byte count does not exceed max array length (minus offset)
    int length = (count > max_length) ? max_length : count;

    // create a new buffer pointer using the offset
    jbyte *offsetBuffer = buffer + offset;

    // perform the actual SPI write operation into the native buffer array
	jint result = spiWrite((unsigned)handle, (char *)offsetBuffer, (unsigned)length);

	// unpin the reserved memory for 'data'; abort preserving any changes back to the Java array
	(*env)->ReleaseByteArrayElements(env, data, buffer, JNI_ABORT);

    // return the result
	return result;
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    spiXfer
 * Signature: (I[B[BI)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_spiXfer
  (JNIEnv *env, jclass class, jint handle, jbyteArray writeData, jint writeOffset, jbyteArray readData, jint readOffset, jint count)
{
	// obtain a pointer to the elements of the array and pin the memory
    jbyte *writeBuffer = (*env)->GetByteArrayElements(env, writeData, 0);
    jbyte *readBuffer = (*env)->GetByteArrayElements(env, readData, 0);

    // get the maximum size of the Java data array; subtract any offset value
    jsize max_length = (*env)->GetArrayLength(env, writeData) - writeOffset;

    // bounds check to make sure byte count does not exceed max array length (minus offset)
    int length = (count > max_length) ? max_length : count;

    // create a new write buffer pointer using the offset
    jbyte *offsetWriteBuffer = writeBuffer + writeOffset;

    // create a new read buffer pointer using the offset
    jbyte *offsetReadBuffer = readBuffer + readOffset;

    // perform the actual SPI read operation into the native buffer array
    jint result = spiXfer((unsigned)handle, (char *)offsetWriteBuffer, (char *)offsetReadBuffer, (unsigned)length);

	// unpin the reserved memory for 'writeData'; abort preserving any changes back to the Java array
	(*env)->ReleaseByteArrayElements(env, writeData, writeBuffer, JNI_ABORT);

	// unpin the reserved memory for 'readData'; persist change to the Java array and free the native array
	(*env)->ReleaseByteArrayElements(env, readData, readBuffer, 0);

    // return the result
	return result;
}


// *****************************************************************************************************
// *****************************************************************************************************
// SERIAL IMPLEMENTATION
// *****************************************************************************************************
// *****************************************************************************************************

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    serOpen
 * Signature: (Ljava/lang/String;II)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_serOpen
  (JNIEnv *env, jclass class, jstring device, jint baud, jint serFlags)
{
    // pin the memory and get a pointer to the underlying char arrat for 'device' string parameter
    const char *device_ptr = (*env)->GetStringUTFChars(env, device, NULL);

    // perform serial port open using PIGPIO library call
    jint result = serOpen((char *)device_ptr, (unsigned)baud, (unsigned)serFlags);

    // release/unpin memory
    (*env)->ReleaseStringUTFChars(env, device, device_ptr);

    // return the result
	return result;
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    serClose
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_serClose
  (JNIEnv *env, jclass class, jint handle)
{
    // perform SERIAL port close using PIGPIO library call
    return serClose((unsigned)handle);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    serWriteByte
 * Signature: (IB)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_serWriteByte
  (JNIEnv *env, jclass class, jint handle, jbyte byt)
{
    // convert value into unsigned int
    unsigned b = byt & 0xFF;

    // perform actual SERIAL write byte operation using PIGPIO library call
    jint result = serWriteByte((unsigned)handle, b);
    return result;
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    serReadByte
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_serReadByte
  (JNIEnv *env, jclass class, jint handle)
{
    // perform actual SERIAL read byte operation using PIGPIO library call
    return serReadByte((unsigned)handle);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    serWrite
 * Signature: (I[BII)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_serWrite
  (JNIEnv *env, jclass class, jint handle, jbyteArray data, jint offset, jint count)
{
	// obtain a pointer to the elements of the array and pin the memory
    jbyte *buffer = (*env)->GetByteArrayElements(env, data, 0);

    // get the maximum size of the Java data array; subtract any offset value
    jsize max_length = (*env)->GetArrayLength(env, data) - offset;

    // bounds check to make sure byte count does not exceed max array length (minus offset)
    int length = (count > max_length) ? max_length : count;

    // create a new buffer pointer using the offset
    jbyte *offsetBuffer = buffer + offset;

    // perform the actual SERIAL write operation into the native buffer array using PIGPIO library call
	jint result = serWrite((unsigned)handle, (char *)offsetBuffer, (unsigned)length);

	// unpin the reserved memory for 'data'; abort preserving any changes back to the Java array
	(*env)->ReleaseByteArrayElements(env, data, buffer, JNI_ABORT);

    // return the result
	return result;
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    serRead
 * Signature: (I[BII)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_serRead
  (JNIEnv *env, jclass class, jint handle, jbyteArray data, jint offset, jint count)
{
	// obtain a pointer to the elements of the array and pin the memory
    jbyte *buffer = (*env)->GetByteArrayElements(env, data, 0);

    // get the maximum size of the Java data array; subtract any offset value
    jsize max_length = (*env)->GetArrayLength(env, data) - offset;

    // bounds check to make sure byte count does not exceed max array length (minus offset)
    int length = (count > max_length) ? max_length : count;

    // create a new buffer pointer using the offset
    jbyte *offsetBuffer = buffer + offset;

    // perform the actual SERIAL read operation into the native buffer array using PIGPIO library call
	jint result = serRead((unsigned)handle, (char *)offsetBuffer, (unsigned)length);

	// unpin the reserved memory for 'data'; persist change to the Java array and free the native array
	(*env)->ReleaseByteArrayElements(env, data, buffer, 0);

    // return the result
	return result;
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    serDataAvailable
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_serDataAvailable
  (JNIEnv *env, jclass class, jint handle)
{
    // perform actual SERIAL get available bytes using PIGPIO library call
    return serDataAvailable((unsigned)handle);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    serDrain
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_serDrain
  (JNIEnv *env, jclass class, jint handle)
{
    // get the number of bytes available
    int available = serDataAvailable((unsigned)handle);

    // only process further if there are bytes available to drain/purge
    if(available > 0){
        // create a temporary buffer
        char buffer[available];

        // read the bytes available into the temporary buffer
        available = serRead((unsigned)handle, buffer, (unsigned)available);

        // result number of bytes drained/purged
        return available;
    }
    else {
        return 0;
    }
}


// *****************************************************************************************************
// *****************************************************************************************************
// DELAY/SLEEP/TIMER IMPLEMENTATION
// *****************************************************************************************************
// *****************************************************************************************************

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioTime
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioTime
  (JNIEnv *env, jclass class, jint timetype, jintArray data)
{
    int seconds = 0;
    int micros = 0;
    int *seconds_ptr = &seconds;
    int *micros_ptr = &micros;

    // get time elements
    jint result = gpioTime((unsigned)timetype, seconds_ptr, micros_ptr);

    // on error; return immediately
    if(result < 0){
        return result;
    }

    // pin memory and get array pointer
    jint *data_ptr = (*env)->GetIntArrayElements(env, data, 0);

    // update array pointer with result time values
    data_ptr[0] = seconds;
    data_ptr[1] = micros;

    // unpin memory
    (*env)->ReleaseIntArrayElements(env, data, data_ptr, 0);

    // return result which should be '0'
    return result;
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioSleep
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioSleep
  (JNIEnv *env, jclass class, jint timetype, jint seconds, jint micros)
{
    return gpioSleep((unsigned)micros, seconds, micros);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioDelay
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioDelay
  (JNIEnv *env, jclass class, jlong micros)
{
    return gpioDelay((uint32_t)micros);
}

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioTick
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioTick
  (JNIEnv *env, jclass class)
{
    return gpioTick();
}


// *****************************************************************************************************
// *****************************************************************************************************
// CONFIGURATION IMPL
// *****************************************************************************************************
// *****************************************************************************************************

/*
 * Class:     com_pi4j_library_pigpio_internal_PIGPIO
 * Method:    gpioCfgInterfaces
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_library_pigpio_internal_PIGPIO_gpioCfgInterfaces
  (JNIEnv *env, jclass class, jint flags)
{
    return gpioCfgInterfaces(flags);
}

