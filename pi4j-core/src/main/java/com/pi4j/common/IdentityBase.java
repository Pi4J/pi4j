package com.pi4j.common;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  IdentityBase.java
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

import com.pi4j.extension.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Abstract IdentityBase class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 * @param <T>
 */
public abstract class IdentityBase<T> implements Extension<T> {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String id;
    protected String name;
    protected String description;
    protected Metadata metadata = Metadata.create();

    /**
     * <p>Constructor for IdentityBase.</p>
     */
    public IdentityBase(){
        this.id = getClass().getName();
        this.name = this.getClass().getSimpleName();
        this.description = this.getClass().getName();
    }

    /**
     * <p>Constructor for IdentityBase.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public IdentityBase(String id){
        this();
        this.id = id;
    }

    /**
     * <p>Constructor for IdentityBase.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @param name a {@link java.lang.String} object.
     */
    public IdentityBase(String id, String name){
        this(id);
        this.name = name;
    }

    /**
     * <p>Constructor for IdentityBase.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @param name a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     */
    public IdentityBase(String id, String name, String description){
        this(id, name);
        this.description = description;
    }

    /** {@inheritDoc} */
    @Override
    public String id() {
        return this.id;
    }

    /** {@inheritDoc} */
    @Override
    public String name() {
        return this.name;
    }

    /** {@inheritDoc} */
    @Override
    public String description() {
        return this.description;
    }

    /** {@inheritDoc} */
    @Override
    public Metadata metadata() {
        return this.metadata;
    }
}
