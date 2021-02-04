/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: JNI Native Library
 * FILENAME      :  com_pi4j_io_file_LinuxFile.h
 * 
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2021 Pi4J
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_pi4j_io_file_LinuxFile */

#ifndef _Included_com_pi4j_io_file_LinuxFile
#define _Included_com_pi4j_io_file_LinuxFile
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     com_pi4j_io_file_LinuxFile
 * Method:    errno
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_io_file_LinuxFile_errno
  (JNIEnv *env, jclass obj);

/*
 * Class:     com_pi4j_io_file_LinuxFile
 * Method:    strerror
 * Signature: (I)Ljava.lang.String;
 */
JNIEXPORT jstring JNICALL Java_com_pi4j_io_file_LinuxFile_strerror
  (JNIEnv *env, jclass obj, jint errorNum);

/*
 * Class:     com_pi4j_io_file_LinuxFile
 * Method:    i2cIOCTL
 * Signature: (IJI)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_io_file_LinuxFile_directIOCTL
  (JNIEnv *env, jclass obj, jint fd, jlong command, jlong value);

/*
 * Class:     com_pi4j_io_file_LinuxFile
 * Method:    mmap
 * Signature: (IIIII)J
 */
JNIEXPORT jlong JNICALL Java_com_pi4j_io_file_LinuxFile_mmap
  (JNIEnv *env, jclass obj, jint fd, jint length, jint prot, jint flags, jint offset);

/*
 * Class:     com_pi4j_io_file_LinuxFile
 * Method:    munmapDirect
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_io_file_LinuxFile_munmapDirect
  (JNIEnv *env, jclass obj, jlong address, jlong capacity);

/*
 * Class:     com_pi4j_io_file_LinuxFile
 * Method:    directIOCTLStructure
 * Signature: (IJLjava.nio.ByteBuffer;ILjava.nio.IntBuffer;II)I
 */
JNIEXPORT jint JNICALL Java_com_pi4j_io_file_LinuxFile_directIOCTLStructure
  (JNIEnv *env, jclass obj, jint fd, jlong command, jobject data, jint dataOffset, jobject offsetMap, jint offsetMapOffset, jint offsetCapacity);

#ifdef __cplusplus
}
#endif
#endif
