package com.pi4j.util;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  ReflectionUtil.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
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

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reflection helpers for discovering the full set of interfaces and superclasses in an object's or class's type
 * hierarchy. Pi4J uses this to inspect the I/O interfaces implemented by a provider or component when wiring up
 * the registry and event handling.
 */
public class ReflectionUtil {

    /**
     * Returns all interfaces implemented anywhere in the given object's type hierarchy, ordered by fully
     * qualified interface name.
     *
     * @param target the object whose runtime class hierarchy is inspected
     * @return the interfaces, sorted by their fully qualified class name
     */
    public static Collection<Class> getAllInterfacesSorted(Object target){
        return getAllInterfaces(target.getClass())
            .stream()
            .sorted(Comparator.comparing(Class::getName))
            .collect(Collectors.toList());
    }

    /**
     * Returns all interfaces implemented anywhere in the given object's type hierarchy.
     *
     * @param target the object whose runtime class hierarchy is inspected
     * @return the set of interfaces, in no particular order
     */
    public static Collection<Class> getAllInterfaces(Object target){
        return getAllInterfaces(target.getClass());
    }

    /**
     * Returns all interfaces reachable from the given class, including those of its superclasses and the
     * super-interfaces of every interface found.
     *
     * @param targetClass the class whose interface hierarchy is traversed
     * @return the set of interfaces, in no particular order
     */
    public static Collection<Class> getAllInterfaces(Class targetClass){
        Set<Class> results = new HashSet<>();

        // get all direct interfaces and their parents
        for(Class ifc : targetClass.getInterfaces()){
            results.add(ifc);
            results.addAll(getAllInterfaces(ifc));
        }

        // get all super classes and their interfaces
        if(targetClass.getSuperclass() != null){
            results.addAll(getAllInterfaces(targetClass.getSuperclass()));
        }

        // get all direct interfaces and their parents
        for(Class ifc : targetClass.getInterfaces()){
            if(ifc.getSuperclass() != null){
                results.addAll(getAllInterfaces(ifc.getSuperclass()));
            }
        }
        return results;
    }

    /**
     * Returns the given object's runtime class together with all of its superclasses.
     *
     * @param target the object whose runtime class hierarchy is inspected
     * @return the set of classes in the hierarchy, in no particular order
     */
    public static Collection<Class> getAllClasses(Object target){
        Set<Class> results = new HashSet<>();
        return getAllClasses(target.getClass());
    }

    /**
     * Returns the given class together with all of its superclasses.
     *
     * @param targetClass the class whose superclass hierarchy is traversed
     * @return the set of classes in the hierarchy, in no particular order
     */
    public static Collection<Class> getAllClasses(Class targetClass){
        Set<Class> results = new HashSet<>();

        results.add(targetClass);

        // get all super classes and their interfaces
        if(targetClass.getSuperclass() != null){
            results.add(targetClass.getSuperclass());
            results.addAll(getAllClasses(targetClass.getSuperclass()));
        }

        return results;
    }

}
