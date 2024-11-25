package com.pi4j.config;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  ConfigBuilder.java
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;

/**
 * <p>ConfigBuilder interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 * @param <BUILDER_TYPE>
 * @param <CONFIG_TYPE>
 */
public interface ConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> extends Builder<CONFIG_TYPE> {
    /**
     * <p>id.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @return a BUILDER_TYPE object.
     */
    BUILDER_TYPE id(String id);

    String id();

    /**
     * <p>name.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a BUILDER_TYPE object.
     */
    BUILDER_TYPE name(String name);
    /**
     * <p>description.</p>
     *
     * @param description a {@link java.lang.String} object.
     * @return a BUILDER_TYPE object.
     */
    BUILDER_TYPE description(String description);

    /**
     * <p>inheritProperties.</p>
     *
     * @param allow a {@link java.lang.Boolean} object.
     * @return a BUILDER_TYPE object.
     */
    BUILDER_TYPE inheritProperties(Boolean allow);

    /**
     * <p>allowInheritProperties.</p>
     *
     * @return a BUILDER_TYPE object.
     */
    default BUILDER_TYPE allowInheritProperties(){
        return inheritProperties(true);
    }

    /**
     * <p>disallowInheritProperties.</p>
     *
     * @return a BUILDER_TYPE object.
     */
    default BUILDER_TYPE disallowInheritProperties(){
        return inheritProperties(false);
    }

    /**
     * <p>load.</p>
     *
     * @param properties a {@link java.util.Map} object.
     * @return a BUILDER_TYPE object.
     */
    BUILDER_TYPE load(Map<String, String> properties);
    /**
     * <p>load.</p>
     *
     * @param properties a {@link java.util.Properties} object.
     * @return a BUILDER_TYPE object.
     */
    BUILDER_TYPE load(Properties properties);
    /**
     * <p>load.</p>
     *
     * @param properties a {@link java.util.Map} object.
     * @param prefixFilter a {@link java.lang.String} object.
     * @return a BUILDER_TYPE object.
     */
    BUILDER_TYPE load(Map<String, String> properties, String prefixFilter);
    /**
     * <p>load.</p>
     *
     * @param properties a {@link java.util.Properties} object.
     * @param prefixFilter a {@link java.lang.String} object.
     * @return a BUILDER_TYPE object.
     */
    BUILDER_TYPE load(Properties properties, String prefixFilter);
    /**
     * <p>load.</p>
     *
     * @param stream a {@link java.io.InputStream} object.
     * @return a BUILDER_TYPE object.
     * @throws java.io.IOException if an error occurs accessing {@code stream}.
     */
    BUILDER_TYPE load(InputStream stream) throws IOException;
    /**
     * <p>load.</p>
     *
     * @param stream a {@link java.io.InputStream} object.
     * @param prefixFilter a {@link java.lang.String} object.
     * @return a BUILDER_TYPE object.
     * @throws java.io.IOException if an error occurs accessing {@code stream}.
     */
    BUILDER_TYPE load(InputStream stream, String prefixFilter) throws IOException;
    /**
     * <p>load.</p>
     *
     * @param reader a {@link java.io.Reader} object.
     * @return a BUILDER_TYPE object.
     * @throws java.io.IOException if an error occurs accessing {@code reader}.
     */
    BUILDER_TYPE load(Reader reader) throws IOException;
    /**
     * <p>load.</p>
     *
     * @param reader a {@link java.io.Reader} object.
     * @param prefixFilter a {@link java.lang.String} object.
     * @return a BUILDER_TYPE object.
     * @throws java.io.IOException if an error occurs accessing {@code reader}.
     */
    BUILDER_TYPE load(Reader reader, String prefixFilter) throws IOException;
    /**
     * <p>load.</p>
     *
     * @param file a {@link java.io.File} object.
     * @return a BUILDER_TYPE object.
     * @throws java.io.IOException if an error occurs accessing {@code file}.
     */
    BUILDER_TYPE load(File file) throws IOException;
    /**
     * <p>load.</p>
     *
     * @param file a {@link java.io.File} object.
     * @param prefixFilter a {@link java.lang.String} object.
     * @return a BUILDER_TYPE object.
     * @throws java.io.IOException if an error occurs accessing {@code file}.
     */
    BUILDER_TYPE load(File file, String prefixFilter) throws IOException;
}
